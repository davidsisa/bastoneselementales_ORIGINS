package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisconnectedRealmsScreen extends RealmsScreen {
    private final Component reason;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    private final Screen parent;
    private int textHeight;

    public DisconnectedRealmsScreen(Screen pParent, Component pTitle, Component pReason) {
        super(pTitle);
        this.parent = pParent;
        this.reason = pReason;
    }

    @Override
    public void init() {
        this.minecraft.getDownloadedPackSource().cleanupAfterDisconnect();
        this.message = MultiLineLabel.create(this.font, this.reason, this.width - 50);
        this.textHeight = this.message.getLineCount() * 9;
        this.addRenderableWidget(
            Button.builder(CommonComponents.GUI_BACK, p_296421_ -> this.minecraft.setScreen(this.parent))
                .bounds(this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + 9, 200, 20)
                .build()
        );
    }

    @Override
    public Component getNarrationMessage() {
        return Component.empty().append(this.title).append(": ").append(this.reason);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
        this.message.renderCentered(pGuiGraphics, this.width / 2, this.height / 2 - this.textHeight / 2);
    }
}