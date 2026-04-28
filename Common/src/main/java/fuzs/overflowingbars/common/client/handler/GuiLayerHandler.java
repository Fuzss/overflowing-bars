package fuzs.overflowingbars.common.client.handler;

import fuzs.overflowingbars.common.OverflowingBars;
import fuzs.overflowingbars.common.client.gui.BarOverlayRenderer;
import fuzs.overflowingbars.common.client.helper.ChatOffsetHelper;
import fuzs.overflowingbars.common.config.ClientConfig;
import fuzs.puzzleslib.common.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.common.api.client.gui.v2.ScreenHelper;
import fuzs.puzzleslib.common.api.event.v1.data.MutableInt;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

public class GuiLayerHandler {
    public static final Identifier TOUGHNESS_LEVEL_LEFT_LOCATION = OverflowingBars.id("toughness_level/left");
    public static final Identifier TOUGHNESS_LEVEL_RIGHT_LOCATION = OverflowingBars.id("toughness_level/right");
    public static final Identifier TOUGHNESS_LEVEL_RIGHT_MOUNTED_LOCATION = OverflowingBars.id(
            "toughness_level/right/mounted");

    public static void onRenderPlayerHealth(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        Player player = getCameraPlayer();
        if (player != null) {
            ClientConfig.IconRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).health;
            int guiHeight = ScreenHelper.getLeftStatusBarHeight(GuiLayersContext.PLAYER_HEALTH);
            guiHeight += config.manualRowShift();
            BarOverlayRenderer.renderHealthLevelBars(guiGraphics, player, guiHeight, config.allowCount);
        }
    }

    public static void onRenderArmorLevel(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        Player player = getCameraPlayer();
        if (player != null) {
            ClientConfig.AbstractArmorRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).armor;
            int guiHeight = ScreenHelper.getLeftStatusBarHeight(GuiLayersContext.ARMOR_LEVEL);
            guiHeight += config.manualRowShift();
            BarOverlayRenderer.renderArmorLevelBar(guiGraphics, player, guiHeight, config.allowCount);
        }
    }

    public static void onRenderToughnessLevel(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, Identifier heightProviderLocation, boolean leftSide) {
        ClientConfig.ToughnessRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).toughness;
        Player player = getCameraPlayer();
        if (config.leftSide == leftSide && config.armorToughnessBar && player != null) {
            int guiHeight;
            if (leftSide) {
                guiHeight = ScreenHelper.getLeftStatusBarHeight(heightProviderLocation);
            } else {
                guiHeight = ScreenHelper.getRightStatusBarHeight(heightProviderLocation);
            }
            guiHeight += config.manualRowShift();
            BarOverlayRenderer.renderToughnessLevelBar(guiGraphics,
                    player,
                    guiHeight,
                    config.allowCount,
                    leftSide,
                    !config.allowToughnessLayers);
        }
    }

    @Nullable
    private static Player getCameraPlayer() {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.hideGui && minecraft.gameMode.canHurtPlayer()) {
            return minecraft.gui.getCameraPlayer();
        } else {
            return null;
        }
    }

    public static void onRenderChatPanel(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, MutableInt posX, MutableInt posY) {
        if (!OverflowingBars.CONFIG.get(ClientConfig.class).armor.moveChatAboveArmor) return;
        posY.mapAsInt((int value) -> value - ChatOffsetHelper.getChatOffsetY());
    }
}
