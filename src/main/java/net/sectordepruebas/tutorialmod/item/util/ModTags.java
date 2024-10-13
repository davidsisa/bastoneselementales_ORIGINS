package net.sectordepruebas.tutorialmod.item.util;
import net.minecraft.world.item.Item;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.sectordepruebas.tutorialmod.bastones;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NECESITA_HERRAMIENTA_ZOMER = createTag("needs_zomer_tool");
        public static final TagKey<Block> INCORRECT__HERRAMIENTA_ZOMER = createTag("incorrect_for_zomer_tool");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(bastones.MOD_ID, name));
        }
    public static class Items {
        public static final TagKey<Item> TRANSFORMABLE_ITEMS = createTag("transformable_items");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(bastones.MOD_ID, name));
        }
        }
    }
}
