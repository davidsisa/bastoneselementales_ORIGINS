package net.minecraft.client.gui.components.toasts;

import java.util.List;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementToast implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");
    public static final int DISPLAY_TIME = 5000;
    private final AdvancementHolder advancement;
    private boolean playedSound;

    public AdvancementToast(AdvancementHolder pAdvancement) {
        this.advancement = pAdvancement;
    }

    @Override
    public Toast.Visibility render(GuiGraphics pGuiGraphics, ToastComponent pToastComponent, long pTimeSinceLastVisible) {
        DisplayInfo displayinfo = this.advancement.value().display().orElse(null);
        pGuiGraphics.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
        if (displayinfo != null) {
            List<FormattedCharSequence> list = pToastComponent.getMinecraft().font.split(displayinfo.getTitle(), 125);
            int i = displayinfo.getType() == AdvancementType.CHALLENGE ? 16746751 : 16776960;
            if (list.size() == 1) {
                pGuiGraphics.drawString(pToastComponent.getMinecraft().font, displayinfo.getType().getDisplayName(), 30, 7, i | 0xFF000000, false);
                pGuiGraphics.drawString(pToastComponent.getMinecraft().font, list.get(0), 30, 18, -1, false);
            } else {
                int j = 1500;
                float f = 300.0F;
                if (pTimeSinceLastVisible < 1500L) {
                    int k = Mth.floor(Mth.clamp((float)(1500L - pTimeSinceLastVisible) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                    pGuiGraphics.drawString(pToastComponent.getMinecraft().font, displayinfo.getType().getDisplayName(), 30, 11, i | k, false);
                } else {
                    int i1 = Mth.floor(Mth.clamp((float)(pTimeSinceLastVisible - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                    int l = this.height() / 2 - list.size() * 9 / 2;

                    for (FormattedCharSequence formattedcharsequence : list) {
                        pGuiGraphics.drawString(pToastComponent.getMinecraft().font, formattedcharsequence, 30, l, 16777215 | i1, false);
                        l += 9;
                    }
                }
            }

            if (!this.playedSound && pTimeSinceLastVisible > 0L) {
                this.playedSound = true;
                if (displayinfo.getType() == AdvancementType.CHALLENGE) {
                    pToastComponent.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
                }
            }

            pGuiGraphics.renderFakeItem(displayinfo.getIcon(), 8, 8);
            return (double)pTimeSinceLastVisible >= 5000.0 * pToastComponent.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        } else {
            return Toast.Visibility.HIDE;
        }
    }
}