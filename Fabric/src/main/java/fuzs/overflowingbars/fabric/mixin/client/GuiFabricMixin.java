package fuzs.overflowingbars.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.helper.HotbarSpriteHelper;
import fuzs.overflowingbars.config.ClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
abstract class GuiFabricMixin {

    @WrapMethod(method = {"renderHotbarAndDecorations", "renderOverlayMessage"})
    private void renderHotbarAndDecorations(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> operation) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, -OverflowingBars.CONFIG.get(ClientConfig.class).hotbarOffset, 0.0F);
        operation.call(guiGraphics, deltaTracker);
        guiGraphics.pose().popPose();
    }

    @WrapMethod(method = "renderExperienceLevel")
    private void renderExperienceLevel(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> operation) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, -OverflowingBars.CONFIG.get(ClientConfig.class).hotbarOffset, 0.0F);
        if (OverflowingBars.CONFIG.get(ClientConfig.class).moveExperienceAboveBar) {
            guiGraphics.pose().translate(0.0F, -3.0F, 0.0F);
        }
        operation.call(guiGraphics, deltaTracker);
        guiGraphics.pose().popPose();
    }

    @Inject(method = "renderItemHotbar", at = @At("TAIL"))
    private void renderItemHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callback) {
        if (this.getCameraPlayer() != null) HotbarSpriteHelper.blitHotbarSelectionSprite(guiGraphics);
    }

    @Shadow
    @Nullable
    private Player getCameraPlayer() {
        throw new RuntimeException();
    }
}
