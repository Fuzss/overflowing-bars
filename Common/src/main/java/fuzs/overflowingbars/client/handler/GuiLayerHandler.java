package fuzs.overflowingbars.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.gui.BarOverlayRenderer;
import fuzs.overflowingbars.client.helper.ChatOffsetHelper;
import fuzs.overflowingbars.config.ClientConfig;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class GuiLayerHandler {

    public static EventResult onRenderPlayerHealth(Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        ClientConfig.IconRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).health;
        if (minecraft.getCameraEntity() instanceof Player && minecraft.gameMode.canHurtPlayer() && config.allowLayers) {
            int guiLeftHeight = ClientAbstractions.INSTANCE.getGuiLeftHeight(minecraft.gui) + config.manualRowShift();
            BarOverlayRenderer.renderHealthLevelBars(minecraft, guiGraphics, guiLeftHeight, config.allowCount);
            BarOverlayRenderer.resetRenderState();
            ClientAbstractions.INSTANCE.addGuiLeftHeight(minecraft.gui,
                    ChatOffsetHelper.twoHealthRows(minecraft.player) ? 20 : 10 + config.manualRowShift()
            );
            return EventResult.INTERRUPT;
        } else {
            return EventResult.PASS;
        }
    }

    public static EventResult onRenderArmorLevel(Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        ClientConfig.AbstractArmorRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).armor;
        if (minecraft.getCameraEntity() instanceof Player && minecraft.gameMode.canHurtPlayer() && config.allowLayers) {
            RenderSystem.enableBlend();
            int guiLeftHeight = ClientAbstractions.INSTANCE.getGuiLeftHeight(minecraft.gui) + config.manualRowShift();
            BarOverlayRenderer.renderArmorLevelBar(minecraft, guiGraphics, guiLeftHeight, config.allowCount, false);
            RenderSystem.disableBlend();
            BarOverlayRenderer.resetRenderState();
            if (ChatOffsetHelper.armorRow(minecraft.player)) {
                ClientAbstractions.INSTANCE.addGuiLeftHeight(minecraft.gui, 10 + config.manualRowShift());
            }
            return EventResult.INTERRUPT;
        } else {
            return EventResult.PASS;
        }
    }

    public static EventResult onRenderToughnessLevel(Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        ClientConfig.ToughnessRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).toughness;
        if (minecraft.getCameraEntity() instanceof Player && minecraft.gameMode.canHurtPlayer() &&
                config.armorToughnessBar) {
            RenderSystem.enableBlend();
            int guiHeight;
            if (config.leftSide) {
                guiHeight = ClientAbstractions.INSTANCE.getGuiLeftHeight(minecraft.gui);
            } else {
                guiHeight = ClientAbstractions.INSTANCE.getGuiRightHeight(minecraft.gui);
            }
            guiHeight += config.manualRowShift();
            BarOverlayRenderer.renderToughnessLevelBar(minecraft, guiGraphics, guiHeight, config.allowCount,
                    config.leftSide, !config.allowLayers
            );
            RenderSystem.disableBlend();
            if (ChatOffsetHelper.toughnessRow(minecraft.player)) {
                if (config.leftSide) {
                    ClientAbstractions.INSTANCE.addGuiLeftHeight(minecraft.gui, 10 + config.manualRowShift());
                } else {
                    ClientAbstractions.INSTANCE.addGuiRightHeight(minecraft.gui, 10 + config.manualRowShift());
                }
            }
        }

        return EventResult.PASS;
    }

    public static void onRenderChatPanel(GuiGraphics guiGraphics, DeltaTracker deltaTracker, MutableInt posX, MutableInt posY) {
        if (!OverflowingBars.CONFIG.get(ClientConfig.class).armor.moveChatAboveArmor) return;
        posY.mapInt(value -> value - ChatOffsetHelper.getChatOffsetY());
    }
}
