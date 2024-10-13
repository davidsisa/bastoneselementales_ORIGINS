package net.sectordepruebas.tutorialmod.item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.sectordepruebas.tutorialmod.item.util.ModTags;
public class ModToolsTiers {
    public static final Tier AMPLITUD_HERRAMIENTAS = new ForgeTier(1400,4,3f,20,
            ModTags.Blocks.NECESITA_HERRAMIENTA_ZOMER,()-> Ingredient.of(ModItems.BASTONDEFUEGO.get()),
            ModTags.Blocks.INCORRECT__HERRAMIENTA_ZOMER);
}
