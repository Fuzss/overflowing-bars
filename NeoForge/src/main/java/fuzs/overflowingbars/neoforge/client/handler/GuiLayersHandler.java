package fuzs.overflowingbars.neoforge.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.handler.BarOverlayRenderer;
import fuzs.overflowingbars.client.helper.ChatOffsetHelper;
import fuzs.overflowingbars.config.ClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.Objects;

public class GuiLayersHandler {
    public static final LayeredDraw.Layer TOUGHNESS_LEVEL = (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
        ClientConfig.ToughnessRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).toughness;
        if (!config.armorToughnessBar) return;
        Minecraft minecraft = Minecraft.getInstance();
        Gui gui = minecraft.gui;
        if (minecraft.gameMode.canHurtPlayer()) {
            RenderSystem.enableBlend();
            BarOverlayRenderer.renderToughnessLevelBar(minecraft, guiGraphics, config.leftSide ? gui.leftHeight : gui.rightHeight, config.allowCount, config.leftSide,
                    !config.allowLayers
            );
            RenderSystem.disableBlend();
            if (ChatOffsetHelper.toughnessRow(minecraft.player)) {
                if (config.leftSide) {
                    gui.leftHeight += 10;
                } else {
                    gui.rightHeight += 10;
                }
            }
        }
    };

    public static void onBeforeRenderGuiOverlay(RenderGuiLayerEvent.Pre evt) {
        handlePlayerHealthOverlay(evt);
        handleArmorLevelOverlay(evt);
    }

    private static void handlePlayerHealthOverlay(RenderGuiLayerEvent.Pre evt) {
        if (Objects.equals(evt.getName(), VanillaGuiLayers.PLAYER_HEALTH) && OverflowingBars.CONFIG.get(
                ClientConfig.class).health.allowLayers) {
            Minecraft minecraft = Minecraft.getInstance();
            Gui gui = minecraft.gui;
            if (minecraft.gameMode.canHurtPlayer()) {
                RenderSystem.enableBlend();
                GuiGraphics guiGraphics = evt.getGuiGraphics();
                BarOverlayRenderer.renderHealthLevelBars(minecraft, guiGraphics, gui.leftHeight,
                        OverflowingBars.CONFIG.get(ClientConfig.class).health.allowCount
                );
                RenderSystem.disableBlend();
                gui.leftHeight += ChatOffsetHelper.twoHealthRows(minecraft.player) ? 20 : 10;
            }
            evt.setCanceled(true);
        }
    }

    private static void handleArmorLevelOverlay(RenderGuiLayerEvent.Pre evt) {
        if (Objects.equals(evt.getName(), VanillaGuiLayers.ARMOR_LEVEL) && OverflowingBars.CONFIG.get(
                ClientConfig.class).armor.allowLayers) {
            Minecraft minecraft = Minecraft.getInstance();
            Gui gui = minecraft.gui;
            if (minecraft.gameMode.canHurtPlayer()) {
                RenderSystem.enableBlend();
                GuiGraphics guiGraphics = evt.getGuiGraphics();
                BarOverlayRenderer.renderArmorLevelBar(minecraft, guiGraphics, gui.leftHeight,
                        OverflowingBars.CONFIG.get(ClientConfig.class).armor.allowCount, false
                );
                RenderSystem.disableBlend();
                if (ChatOffsetHelper.armorRow(minecraft.player)) {
                    gui.leftHeight += 10;
                }
            }
            evt.setCanceled(true);
        }
    }
}
