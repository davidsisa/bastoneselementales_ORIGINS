package net.minecraft.world.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

public abstract class AbstractMinecartContainer extends AbstractMinecart implements ContainerEntity {
    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
    @Nullable
    private ResourceKey<LootTable> lootTable;
    private long lootTableSeed;

    protected AbstractMinecartContainer(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected AbstractMinecartContainer(EntityType<?> pEntityType, double pX, double pY, double pZ, Level pLevel) {
        super(pEntityType, pLevel, pX, pY, pZ);
    }

    @Override
    public void destroy(DamageSource pSource) {
        super.destroy(pSource);
        this.chestVehicleDestroyed(pSource, this.level(), this);
    }

    @Override
    public ItemStack getItem(int pIndex) {
        return this.getChestVehicleItem(pIndex);
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        return this.removeChestVehicleItem(pIndex, pCount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pIndex) {
        return this.removeChestVehicleItemNoUpdate(pIndex);
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        this.setChestVehicleItem(pIndex, pStack);
    }

    @Override
    public SlotAccess getSlot(int pSlot) {
        return this.getChestVehicleSlot(pSlot);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.isChestVehicleStillValid(pPlayer);
    }

    @Override
    public void remove(Entity.RemovalReason pReason) {
        if (!this.level().isClientSide && pReason.shouldDestroy()) {
            Containers.dropContents(this.level(), this, this);
        }

        super.remove(pReason);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.addChestVehicleSaveData(pCompound, this.registryAccess());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.readChestVehicleSaveData(pCompound, this.registryAccess());
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        var ret = super.interact(pPlayer, pHand);
        if (ret.consumesAction()) return ret;
        return this.interactWithContainerVehicle(pPlayer);
    }

    @Override
    protected void applyNaturalSlowdown() {
        float f = 0.98F;
        if (this.lootTable == null) {
            int i = 15 - AbstractContainerMenu.getRedstoneSignalFromContainer(this);
            f += (float)i * 0.001F;
        }

        if (this.isInWater()) {
            f *= 0.95F;
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply((double)f, 0.0, (double)f));
    }

    @Override
    public void clearContent() {
        this.clearChestVehicleContent();
    }

    public void setLootTable(ResourceKey<LootTable> pLootTable, long pSeed) {
        this.lootTable = pLootTable;
        this.lootTableSeed = pSeed;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        if (this.lootTable != null && pPlayer.isSpectator()) {
            return null;
        } else {
            this.unpackChestVehicleLootTable(pPlayerInventory.player);
            return this.createMenu(pContainerId, pPlayerInventory);
        }
    }

    protected abstract AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory);

    @Nullable
    @Override
    public ResourceKey<LootTable> getLootTable() {
        return this.lootTable;
    }

    @Override
    public void setLootTable(@Nullable ResourceKey<LootTable> pLootTable) {
        this.lootTable = pLootTable;
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long pLootTableSeed) {
        this.lootTableSeed = pLootTableSeed;
    }

    @Override
    public NonNullList<ItemStack> getItemStacks() {
        return this.itemStacks;
    }

    @Override
    public void clearItemStacks() {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.core.Direction facing) {
        if (capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER && this.isAlive()) {
            return itemHandler.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this));
    }
}
