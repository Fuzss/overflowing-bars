package fuzs.overflowingbars.common.client;

import fuzs.overflowingbars.common.OverflowingBars;
import fuzs.overflowingbars.common.client.gui.HealthBarRenderer;
import fuzs.overflowingbars.common.client.handler.GuiLayerHandler;
import fuzs.overflowingbars.common.client.helper.ChatOffsetHelper;
import fuzs.overflowingbars.common.client.init.ModEnumConstants;
import fuzs.overflowingbars.common.config.ClientConfig;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.common.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.common.api.client.event.v1.gui.CustomizeChatPanelCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Player;

public class OverflowingBarsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        ModEnumConstants.bootstrap();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientTickEvents.START.register(HealthBarRenderer.INSTANCE::onStartTick);
        CustomizeChatPanelCallback.EVENT.register(GuiLayerHandler::onRenderChatPanel);
    }

    @Override
    public void onRegisterGuiLayers(GuiLayersContext context) {
        if (OverflowingBars.CONFIG.get(ClientConfig.class).health.allowHealthLayers) {
            context.replaceGuiLayer(GuiLayersContext.PLAYER_HEALTH, (GuiLayersContext.Layer layer) -> {
                return GuiLayerHandler::onRenderPlayerHealth;
            });
            context.addLeftStatusBarHeightProvider(GuiLayersContext.PLAYER_HEALTH, (Player player) -> {
                return (ChatOffsetHelper.twoHealthRows(player) ? 20 : 10)
                        + OverflowingBars.CONFIG.get(ClientConfig.class).health.manualRowShift();
            });
        }
        if (OverflowingBars.CONFIG.get(ClientConfig.class).armor.allowArmorLayers) {
            context.replaceGuiLayer(GuiLayersContext.ARMOR_LEVEL, (GuiLayersContext.Layer layer) -> {
                return GuiLayerHandler::onRenderArmorLevel;
            });
            context.addLeftStatusBarHeightProvider(GuiLayersContext.ARMOR_LEVEL, (Player player) -> {
                if (ChatOffsetHelper.armorRow(player)) {
                    return 10 + OverflowingBars.CONFIG.get(ClientConfig.class).armor.manualRowShift();
                } else {
                    return 0;
                }
            });
        }
        context.registerGuiLayer(GuiLayersContext.ARMOR_LEVEL,
                GuiLayerHandler.TOUGHNESS_LEVEL_LEFT_ID,
                (GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) -> {
                    GuiLayerHandler.onRenderToughnessLevel(guiGraphics,
                            deltaTracker,
                            GuiLayerHandler.TOUGHNESS_LEVEL_LEFT_ID,
                            true);
                });
        context.registerGuiLayer(GuiLayersContext.FOOD_LEVEL,
                GuiLayerHandler.TOUGHNESS_LEVEL_RIGHT_ID,
                (GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) -> {
                    Hud hud = Minecraft.getInstance().gui.hud;
                    int vehicleMaxHearts = hud.getVehicleMaxHearts(hud.getPlayerVehicleWithHealth());
                    if (vehicleMaxHearts == 0) {
                        GuiLayerHandler.onRenderToughnessLevel(guiGraphics,
                                deltaTracker,
                                GuiLayerHandler.TOUGHNESS_LEVEL_RIGHT_ID,
                                false);
                    }
                });
        context.registerGuiLayer(GuiLayersContext.VEHICLE_HEALTH,
                GuiLayerHandler.TOUGHNESS_LEVEL_RIGHT_MOUNTED_ID,
                (GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) -> {
                    Hud hud = Minecraft.getInstance().gui.hud;
                    int vehicleMaxHearts = hud.getVehicleMaxHearts(hud.getPlayerVehicleWithHealth());
                    if (vehicleMaxHearts != 0) {
                        GuiLayerHandler.onRenderToughnessLevel(guiGraphics,
                                deltaTracker,
                                GuiLayerHandler.TOUGHNESS_LEVEL_RIGHT_MOUNTED_ID,
                                false);
                    }
                });
        context.addLeftStatusBarHeightProvider(GuiLayerHandler.TOUGHNESS_LEVEL_LEFT_ID, (Player player) -> {
            if (ChatOffsetHelper.toughnessRow(player)
                    && OverflowingBars.CONFIG.get(ClientConfig.class).toughness.leftSide) {
                return 10 + OverflowingBars.CONFIG.get(ClientConfig.class).toughness.manualRowShift();
            } else {
                return 0;
            }
        });
        context.addRightStatusBarHeightProvider(GuiLayerHandler.TOUGHNESS_LEVEL_RIGHT_ID, (Player player) -> {
            Hud hud = Minecraft.getInstance().gui.hud;
            int vehicleMaxHearts = hud.getVehicleMaxHearts(hud.getPlayerVehicleWithHealth());
            if (vehicleMaxHearts == 0 && ChatOffsetHelper.toughnessRow(player) && !OverflowingBars.CONFIG.get(
                    ClientConfig.class).toughness.leftSide) {
                return 10 + OverflowingBars.CONFIG.get(ClientConfig.class).toughness.manualRowShift();
            } else {
                return 0;
            }
        });
        context.addRightStatusBarHeightProvider(GuiLayerHandler.TOUGHNESS_LEVEL_RIGHT_MOUNTED_ID, (Player player) -> {
            Hud hud = Minecraft.getInstance().gui.hud;
            int vehicleMaxHearts = hud.getVehicleMaxHearts(hud.getPlayerVehicleWithHealth());
            if (vehicleMaxHearts != 0 && ChatOffsetHelper.toughnessRow(player) && !OverflowingBars.CONFIG.get(
                    ClientConfig.class).toughness.leftSide) {
                return 10 + OverflowingBars.CONFIG.get(ClientConfig.class).toughness.manualRowShift();
            } else {
                return 0;
            }
        });
    }
}
