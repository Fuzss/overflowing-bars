package fuzs.overflowingbars.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.config.ClientConfig;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
abstract class GuiMixin {

    @Inject(method = "renderExperienceBar", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/LocalPlayer;experienceLevel:I", ordinal = 0))
    public void renderExperienceBar$0(PoseStack poseStack, int xPos, CallbackInfo callback) {
        if (!OverflowingBars.CONFIG.get(ClientConfig.class).moveExperienceAboveBar) return;
        poseStack.pushPose();
        poseStack.translate(0.0, -3.0, 0.0);
    }

    @Inject(method = "renderExperienceBar", at = @At(value = "TAIL"))
    public void renderExperienceBar$1(PoseStack poseStack, int xPos, CallbackInfo callback) {
        if (!OverflowingBars.CONFIG.get(ClientConfig.class).moveExperienceAboveBar) return;
        poseStack.popPose();
    }
}
