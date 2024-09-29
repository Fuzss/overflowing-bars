package fuzs.overflowingbars.neoforge.client.handler;

import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.helper.HotbarSpriteHelper;
import fuzs.overflowingbars.config.ClientConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.Objects;
import java.util.Set;

public class HoveringHotbarHandler {
    /**
     * We do not need to include {@link VanillaGuiLayers#SELECTED_ITEM_NAME} &amp;
     * {@link VanillaGuiLayers#OVERLAY_MESSAGE}, both get their render height from {@link Gui#leftHeight} &amp;
     * {@link Gui#rightHeight}.
     */
    private static final Set<ResourceLocation> HOTBAR_GUI_LAYERS = Set.of(VanillaGuiLayers.HOTBAR,
            VanillaGuiLayers.JUMP_METER, VanillaGuiLayers.EXPERIENCE_BAR, VanillaGuiLayers.SPECTATOR_TOOLTIP,
            VanillaGuiLayers.EXPERIENCE_LEVEL
    );
    private static boolean pushed;

    public static void onBeforeRenderGui(final RenderGuiEvent.Pre evt) {
        pushed = false;
        evt.getGuiGraphics().pose().pushPose();
//        Gui gui = Minecraft.getInstance().gui;
//        gui.leftHeight += HOTBAR_Y_OFFSET;
//        gui.rightHeight += HOTBAR_Y_OFFSET;
    }

    public static void onAfterRenderGui(final RenderGuiEvent.Post evt) {
        evt.getGuiGraphics().pose().popPose();
    }

    public static void onBeforeRenderGuiLayer(RenderGuiLayerEvent.Pre evt) {
        GuiGraphics guiGraphics = evt.getGuiGraphics();
        if (HOTBAR_GUI_LAYERS.contains(evt.getName())) {
            if (!pushed) {
                pushed = true;
                guiGraphics.pose().translate(0.0F, -OverflowingBars.CONFIG.get(ClientConfig.class).hotbarOffset, 0.0F);
            }
        } else if (pushed) {
            pushed = false;
            guiGraphics.pose().translate(0.0F, OverflowingBars.CONFIG.get(ClientConfig.class).hotbarOffset, 0.0F);
        }

        if (Objects.equals(VanillaGuiLayers.HOTBAR, evt.getName())) {
            HotbarSpriteHelper.blitHotbarSelectionSprite(guiGraphics);
        }
        handleExperienceLevelOverlay(evt);
    }

    private static void handleExperienceLevelOverlay(RenderGuiLayerEvent.Pre evt) {
        if (Objects.equals(evt.getName(), VanillaGuiLayers.EXPERIENCE_LEVEL) && OverflowingBars.CONFIG.get(
                ClientConfig.class).moveExperienceAboveBar) {
            GuiGraphics guiGraphics = evt.getGuiGraphics();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0, -3.0, 0.0);
            evt.getLayer().render(guiGraphics, evt.getPartialTick());
            guiGraphics.pose().popPose();
            evt.setCanceled(true);
        }
    }
}
