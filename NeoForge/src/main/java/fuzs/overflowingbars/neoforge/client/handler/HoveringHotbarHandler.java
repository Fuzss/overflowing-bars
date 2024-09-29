package fuzs.overflowingbars.neoforge.client.handler;

import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.helper.HotbarSpriteHelper;
import fuzs.overflowingbars.config.ClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class HoveringHotbarHandler {
    private static boolean isOffsetApplied;

    public static void onBeforeRenderGui(final RenderGuiEvent.Pre evt) {
        isOffsetApplied = false;
        evt.getGuiGraphics().pose().pushPose();
    }

    public static void onAfterRenderGui(final RenderGuiEvent.Post evt) {
        evt.getGuiGraphics().pose().popPose();
    }

    public static void onBeforeRenderGuiLayer(RenderGuiLayerEvent.Pre evt) {
        applyHotbarOffset(evt);
        if (evt.getName().equals(VanillaGuiLayers.HOTBAR)) {
            HotbarSpriteHelper.blitHotbarSelectionSprite(evt.getGuiGraphics());
        }
        if (evt.getName().equals(VanillaGuiLayers.EXPERIENCE_LEVEL)) {
            renderExperienceLevel(evt);
        }
    }

    private static void applyHotbarOffset(RenderGuiLayerEvent.Pre evt) {
        if (OverflowingBars.CONFIG.get(ClientConfig.class).hotbarGuiLayers.contains(evt.getName())) {
            if (!isOffsetApplied) {
                isOffsetApplied = true;
                evt.getGuiGraphics().pose().translate(0.0F,
                        -OverflowingBars.CONFIG.get(ClientConfig.class).hotbarOffset, 0.0F
                );
            }
        } else if (isOffsetApplied) {
            isOffsetApplied = false;
            evt.getGuiGraphics().pose().translate(0.0F, OverflowingBars.CONFIG.get(ClientConfig.class).hotbarOffset,
                    0.0F
            );
        }
    }

    private static void renderExperienceLevel(RenderGuiLayerEvent.Pre evt) {
        if (OverflowingBars.CONFIG.get(ClientConfig.class).moveExperienceAboveBar) {
            GuiGraphics guiGraphics = evt.getGuiGraphics();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0, -3.0, 0.0);
            // we render the layer manually, to avoid another mod potentially cancelling this,
            // without us having a chance to pop the pose stack after wards
            evt.getLayer().render(guiGraphics, evt.getPartialTick());
            guiGraphics.pose().popPose();
            evt.setCanceled(true);
        }
    }
}
