package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MerchantScreen extends AbstractContainerScreen<MerchantMenu> {
    private static final ResourceLocation OUT_OF_STOCK_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/out_of_stock");
    private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/experience_bar_background");
    private static final ResourceLocation EXPERIENCE_BAR_CURRENT_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/experience_bar_current");
    private static final ResourceLocation EXPERIENCE_BAR_RESULT_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/experience_bar_result");
    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/scroller_disabled");
    private static final ResourceLocation TRADE_ARROW_OUT_OF_STOCK_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/trade_arrow_out_of_stock");
    private static final ResourceLocation TRADE_ARROW_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/trade_arrow");
    private static final ResourceLocation DISCOUNT_STRIKETHRUOGH_SPRITE = ResourceLocation.withDefaultNamespace("container/villager/discount_strikethrough");
    private static final ResourceLocation VILLAGER_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/villager.png");
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int MERCHANT_MENU_PART_X = 99;
    private static final int PROGRESS_BAR_X = 136;
    private static final int PROGRESS_BAR_Y = 16;
    private static final int SELL_ITEM_1_X = 5;
    private static final int SELL_ITEM_2_X = 35;
    private static final int BUY_ITEM_X = 68;
    private static final int LABEL_Y = 6;
    private static final int NUMBER_OF_OFFER_BUTTONS = 7;
    private static final int TRADE_BUTTON_X = 5;
    private static final int TRADE_BUTTON_HEIGHT = 20;
    private static final int TRADE_BUTTON_WIDTH = 88;
    private static final int SCROLLER_HEIGHT = 27;
    private static final int SCROLLER_WIDTH = 6;
    private static final int SCROLL_BAR_HEIGHT = 139;
    private static final int SCROLL_BAR_TOP_POS_Y = 18;
    private static final int SCROLL_BAR_START_X = 94;
    private static final Component TRADES_LABEL = Component.translatable("merchant.trades");
    private static final Component DEPRECATED_TOOLTIP = Component.translatable("merchant.deprecated");
    private int shopItem;
    private final MerchantScreen.TradeOfferButton[] tradeOfferButtons = new MerchantScreen.TradeOfferButton[7];
    int scrollOff;
    private boolean isDragging;

    public MerchantScreen(MerchantMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 276;
        this.inventoryLabelX = 107;
    }

    private void postButtonClick() {
        this.menu.setSelectionHint(this.shopItem);
        this.menu.tryMoveItems(this.shopItem);
        this.minecraft.getConnection().send(new ServerboundSelectTradePacket(this.shopItem));
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int k = j + 16 + 2;

        for (int l = 0; l < 7; l++) {
            this.tradeOfferButtons[l] = this.addRenderableWidget(new MerchantScreen.TradeOfferButton(i + 5, k, l, p_99174_ -> {
                if (p_99174_ instanceof MerchantScreen.TradeOfferButton) {
                    this.shopItem = ((MerchantScreen.TradeOfferButton)p_99174_).getIndex() + this.scrollOff;
                    this.postButtonClick();
                }
            }));
            k += 20;
        }
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        int i = this.menu.getTraderLevel();
        if (i > 0 && i <= 5 && this.menu.showProgressBar()) {
            Component component = Component.translatable("merchant.title", this.title, Component.translatable("merchant.level." + i));
            int j = this.font.width(component);
            int k = 49 + this.imageWidth / 2 - j / 2;
            pGuiGraphics.drawString(this.font, component, k, 6, 4210752, false);
        } else {
            pGuiGraphics.drawString(this.font, this.title, 49 + this.imageWidth / 2 - this.font.width(this.title) / 2, 6, 4210752, false);
        }

        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
        int l = this.font.width(TRADES_LABEL);
        pGuiGraphics.drawString(this.font, TRADES_LABEL, 5 - l / 2 + 48, 6, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(VILLAGER_LOCATION, i, j, 0, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
        MerchantOffers merchantoffers = this.menu.getOffers();
        if (!merchantoffers.isEmpty()) {
            int k = this.shopItem;
            if (k < 0 || k >= merchantoffers.size()) {
                return;
            }

            MerchantOffer merchantoffer = merchantoffers.get(k);
            if (merchantoffer.isOutOfStock()) {
                pGuiGraphics.blitSprite(OUT_OF_STOCK_SPRITE, this.leftPos + 83 + 99, this.topPos + 35, 0, 28, 21);
            }
        }
    }

    private void renderProgressBar(GuiGraphics pGuiGraphics, int pPosX, int pPosY, MerchantOffer pMerchantOffer) {
        int i = this.menu.getTraderLevel();
        int j = this.menu.getTraderXp();
        if (i < 5) {
            pGuiGraphics.blitSprite(EXPERIENCE_BAR_BACKGROUND_SPRITE, pPosX + 136, pPosY + 16, 0, 102, 5);
            int k = VillagerData.getMinXpPerLevel(i);
            if (j >= k && VillagerData.canLevelUp(i)) {
                int l = 102;
                float f = 102.0F / (float)(VillagerData.getMaxXpPerLevel(i) - k);
                int i1 = Math.min(Mth.floor(f * (float)(j - k)), 102);
                pGuiGraphics.blitSprite(EXPERIENCE_BAR_CURRENT_SPRITE, 102, 5, 0, 0, pPosX + 136, pPosY + 16, 0, i1, 5);
                int j1 = this.menu.getFutureTraderXp();
                if (j1 > 0) {
                    int k1 = Math.min(Mth.floor((float)j1 * f), 102 - i1);
                    pGuiGraphics.blitSprite(EXPERIENCE_BAR_RESULT_SPRITE, 102, 5, i1, 0, pPosX + 136 + i1, pPosY + 16, 0, k1, 5);
                }
            }
        }
    }

    private void renderScroller(GuiGraphics pGuiGraphics, int pPosX, int pPosY, MerchantOffers pMerchantOffers) {
        int i = pMerchantOffers.size() + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int l = 113;
            int i1 = Math.min(113, this.scrollOff * k);
            if (this.scrollOff == i - 1) {
                i1 = 113;
            }

            pGuiGraphics.blitSprite(SCROLLER_SPRITE, pPosX + 94, pPosY + 18 + i1, 0, 6, 27);
        } else {
            pGuiGraphics.blitSprite(SCROLLER_DISABLED_SPRITE, pPosX + 94, pPosY + 18, 0, 6, 27);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        MerchantOffers merchantoffers = this.menu.getOffers();
        if (!merchantoffers.isEmpty()) {
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            this.renderScroller(pGuiGraphics, i, j, merchantoffers);
            int i1 = 0;

            for (MerchantOffer merchantoffer : merchantoffers) {
                if (!this.canScroll(merchantoffers.size()) || i1 >= this.scrollOff && i1 < 7 + this.scrollOff) {
                    ItemStack itemstack = merchantoffer.getBaseCostA();
                    ItemStack itemstack1 = merchantoffer.getCostA();
                    ItemStack itemstack2 = merchantoffer.getCostB();
                    ItemStack itemstack3 = merchantoffer.getResult();
                    pGuiGraphics.pose().pushPose();
                    pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
                    int j1 = k + 2;
                    this.renderAndDecorateCostA(pGuiGraphics, itemstack1, itemstack, l, j1);
                    if (!itemstack2.isEmpty()) {
                        pGuiGraphics.renderFakeItem(itemstack2, i + 5 + 35, j1);
                        pGuiGraphics.renderItemDecorations(this.font, itemstack2, i + 5 + 35, j1);
                    }

                    this.renderButtonArrows(pGuiGraphics, merchantoffer, i, j1);
                    pGuiGraphics.renderFakeItem(itemstack3, i + 5 + 68, j1);
                    pGuiGraphics.renderItemDecorations(this.font, itemstack3, i + 5 + 68, j1);
                    pGuiGraphics.pose().popPose();
                    k += 20;
                    i1++;
                } else {
                    i1++;
                }
            }

            int k1 = this.shopItem;
            MerchantOffer merchantoffer1 = merchantoffers.get(k1);
            if (this.menu.showProgressBar()) {
                this.renderProgressBar(pGuiGraphics, i, j, merchantoffer1);
            }

            if (merchantoffer1.isOutOfStock() && this.isHovering(186, 35, 22, 21, (double)pMouseX, (double)pMouseY) && this.menu.canRestock()) {
                pGuiGraphics.renderTooltip(this.font, DEPRECATED_TOOLTIP, pMouseX, pMouseY);
            }

            for (MerchantScreen.TradeOfferButton merchantscreen$tradeofferbutton : this.tradeOfferButtons) {
                if (merchantscreen$tradeofferbutton.isHoveredOrFocused()) {
                    merchantscreen$tradeofferbutton.renderToolTip(pGuiGraphics, pMouseX, pMouseY);
                }

                merchantscreen$tradeofferbutton.visible = merchantscreen$tradeofferbutton.index < this.menu.getOffers().size();
            }

            RenderSystem.enableDepthTest();
        }

        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    private void renderButtonArrows(GuiGraphics pGuiGraphics, MerchantOffer pMerchantOffers, int pPosX, int pPosY) {
        RenderSystem.enableBlend();
        if (pMerchantOffers.isOutOfStock()) {
            pGuiGraphics.blitSprite(TRADE_ARROW_OUT_OF_STOCK_SPRITE, pPosX + 5 + 35 + 20, pPosY + 3, 0, 10, 9);
        } else {
            pGuiGraphics.blitSprite(TRADE_ARROW_SPRITE, pPosX + 5 + 35 + 20, pPosY + 3, 0, 10, 9);
        }
    }

    private void renderAndDecorateCostA(GuiGraphics pGuiGraphics, ItemStack pRealCost, ItemStack pBaseCost, int pX, int pY) {
        pGuiGraphics.renderFakeItem(pRealCost, pX, pY);
        if (pBaseCost.getCount() == pRealCost.getCount()) {
            pGuiGraphics.renderItemDecorations(this.font, pRealCost, pX, pY);
        } else {
            pGuiGraphics.renderItemDecorations(this.font, pBaseCost, pX, pY, pBaseCost.getCount() == 1 ? "1" : null);
            // Forge: fixes Forge-8806, code for count rendering taken from GuiGraphics#renderGuiItemDecorations
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
            String count = pRealCost.getCount() == 1 ? "1" : String.valueOf(pRealCost.getCount());
            font.drawInBatch(count, (float) (pX + 14) + 19 - 2 - font.width(count), (float)pY + 6 + 3, 0xFFFFFF, true, pGuiGraphics.pose().last().pose(), pGuiGraphics.bufferSource(), net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, 15728880, false);
            pGuiGraphics.pose().popPose();
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0.0F, 0.0F, 300.0F);
            pGuiGraphics.blitSprite(DISCOUNT_STRIKETHRUOGH_SPRITE, pX + 7, pY + 12, 0, 9, 2);
            pGuiGraphics.pose().popPose();
        }
    }

    private boolean canScroll(int pNumOffers) {
        return pNumOffers > 7;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        int i = this.menu.getOffers().size();
        if (this.canScroll(i)) {
            int j = i - 7;
            this.scrollOff = Mth.clamp((int)((double)this.scrollOff - pScrollY), 0, j);
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        int i = this.menu.getOffers().size();
        if (this.isDragging) {
            int j = this.topPos + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float)pMouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
            f = f * (float)l + 0.5F;
            this.scrollOff = Mth.clamp((int)f, 0, l);
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.isDragging = false;
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (this.canScroll(this.menu.getOffers().size())
            && pMouseX > (double)(i + 94)
            && pMouseX < (double)(i + 94 + 6)
            && pMouseY > (double)(j + 18)
            && pMouseY <= (double)(j + 18 + 139 + 1)) {
            this.isDragging = true;
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @OnlyIn(Dist.CLIENT)
    class TradeOfferButton extends Button {
        final int index;

        public TradeOfferButton(final int pX, final int pY, final int pIndex, final Button.OnPress pOnPress) {
            super(pX, pY, 88, 20, CommonComponents.EMPTY, pOnPress, DEFAULT_NARRATION);
            this.index = pIndex;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        public void renderToolTip(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
            if (this.isHovered && MerchantScreen.this.menu.getOffers().size() > this.index + MerchantScreen.this.scrollOff) {
                if (pMouseX < this.getX() + 20) {
                    ItemStack itemstack = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getCostA();
                    pGuiGraphics.renderTooltip(MerchantScreen.this.font, itemstack, pMouseX, pMouseY);
                } else if (pMouseX < this.getX() + 50 && pMouseX > this.getX() + 30) {
                    ItemStack itemstack2 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getCostB();
                    if (!itemstack2.isEmpty()) {
                        pGuiGraphics.renderTooltip(MerchantScreen.this.font, itemstack2, pMouseX, pMouseY);
                    }
                } else if (pMouseX > this.getX() + 65) {
                    ItemStack itemstack1 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getResult();
                    pGuiGraphics.renderTooltip(MerchantScreen.this.font, itemstack1, pMouseX, pMouseY);
                }
            }
        }
    }
}
