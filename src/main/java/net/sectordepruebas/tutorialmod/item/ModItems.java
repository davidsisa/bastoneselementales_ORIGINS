package net.sectordepruebas.tutorialmod.item;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sectordepruebas.tutorialmod.bastones;
import net.minecraftforge.eventbus.api.IEventBus;
public class ModItems {

    /*
    * Registrar los objetos tipo ITEMS que vamos a crear
    * Dependiendo del tipo de item que vamos a crear cambiamos ITEMS
    */
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, bastones.MOD_ID);
    public static final RegistryObject<Item> BASTONDEFUEGO = ITEMS.register("bastondefuego",
            () -> new SwordItem(ModToolsTiers.AMPLITUD_HERRAMIENTAS,new Item.Properties()));
    public static void register (IEventBus eventBus) {
            ITEMS.register(eventBus);
    }

}
