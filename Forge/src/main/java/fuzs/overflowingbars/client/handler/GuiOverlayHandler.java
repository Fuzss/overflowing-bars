package fuzs.overflowingbars.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.helper.ChatOffsetHelper;
import fuzs.overflowingbars.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

public class GuiOverlayHandler {
    public static final IIngameOverlay TOUGHNESS_LEVEL = (ForgeIngameGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) -> {
        ClientConfig.ToughnessRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).toughness;
        if (!config.armorToughnessBar) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements()) {
            RenderSystem.enableBlend();
            BarOverlayRenderer.renderToughnessLevelBar(poseStack, screenWidth, screenHeight, minecraft, config.leftSide ? gui.left_height : gui.right_height, config.allowCount, config.leftSide, !config.allowLayers);
            RenderSystem.disableBlend();
            if (ChatOffsetHelper.toughnessRow(minecraft.player)) {
                if (config.leftSide) {
                    gui.left_height += 10;
                } else {
                    gui.right_height += 10;
                }
            }
        }
    };

    public static void onBeforeRenderGuiOverlay(RenderGameOverlayEvent.PreLayer evt) {
        handlePlayerHealthOverlay(evt);
        handleArmorLevelOverlay(evt);
    }

    private static void handlePlayerHealthOverlay(RenderGameOverlayEvent.PreLayer evt) {
        if (evt.getOverlay() == ForgeIngameGui.PLAYER_HEALTH_ELEMENT && OverflowingBars.CONFIG.get(ClientConfig.class).health.allowLayers) {
            Minecraft minecraft = Minecraft.getInstance();
            ForgeIngameGui gui = ((ForgeIngameGui) minecraft.gui);
            if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements()) {
                RenderSystem.enableBlend();
                BarOverlayRenderer.renderHealthLevelBars(evt.getMatrixStack(), evt.getWindow().getGuiScaledWidth(), evt.getWindow().getGuiScaledHeight(), minecraft, gui.left_height, OverflowingBars.CONFIG.get(ClientConfig.class).health.allowCount);
                RenderSystem.disableBlend();
                gui.left_height += ChatOffsetHelper.twoHealthRows(minecraft.player) ? 20 : 10;
            }
            evt.setCanceled(true);
        }
    }

    private static void handleArmorLevelOverlay(RenderGameOverlayEvent.PreLayer evt) {
        if (evt.getOverlay() == ForgeIngameGui.ARMOR_LEVEL_ELEMENT && OverflowingBars.CONFIG.get(ClientConfig.class).armor.allowLayers) {
            Minecraft minecraft = Minecraft.getInstance();
            ForgeIngameGui gui = ((ForgeIngameGui) minecraft.gui);
            if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements()) {
                RenderSystem.enableBlend();
                BarOverlayRenderer.renderArmorLevelBar(evt.getMatrixStack(), evt.getWindow().getGuiScaledWidth(), evt.getWindow().getGuiScaledHeight(), minecraft, gui.left_height, OverflowingBars.CONFIG.get(ClientConfig.class).armor.allowCount, false);
                RenderSystem.disableBlend();
                if (ChatOffsetHelper.armorRow(minecraft.player)) {
                    gui.left_height += 10;
                }
            }
            evt.setCanceled(true);
        }
    }
}
