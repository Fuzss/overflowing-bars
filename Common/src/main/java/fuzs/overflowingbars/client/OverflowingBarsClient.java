package fuzs.overflowingbars.client;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.handler.BarOverlayRenderer;
import fuzs.overflowingbars.client.handler.HealthBarRenderer;
import fuzs.overflowingbars.client.helper.ChatOffsetHelper;
import fuzs.overflowingbars.config.ClientConfig;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.CustomizeChatPanelCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class OverflowingBarsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientTickEvents.START.register(HealthBarRenderer.INSTANCE::onStartTick);
        CustomizeChatPanelCallback.EVENT.register(
                (GuiGraphics guiGraphics, DeltaTracker deltaTracker, MutableInt posX, MutableInt posY) -> {
                    posY.mapInt(value -> value - ChatOffsetHelper.getChatOffsetY());
                });
        CustomizeChatPanelCallback.EVENT.register(
                (GuiGraphics guiGraphics, DeltaTracker deltaTracker, MutableInt posX, MutableInt posY) -> {
                    posY.mapInt(value -> value - OverflowingBars.CONFIG.get(ClientConfig.class).hotbarOffset);
                });
        RenderGuiLayerEvents.before(RenderGuiLayerEvents.PLAYER_HEALTH).register(
                (Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                    if (minecraft.getCameraEntity() instanceof Player && OverflowingBars.CONFIG.get(
                            ClientConfig.class).health.allowLayers) {
                        int guiLeftHeight = ClientAbstractions.INSTANCE.getGuiLeftHeight(minecraft.gui);
                        BarOverlayRenderer.renderHealthLevelBars(minecraft, guiGraphics, guiLeftHeight,
                                OverflowingBars.CONFIG.get(ClientConfig.class).health.allowCount
                        );
                        BarOverlayRenderer.resetRenderState();
                        ClientAbstractions.INSTANCE.addGuiLeftHeight(minecraft.gui,
                                ChatOffsetHelper.twoHealthRows(minecraft.player) ? 20 : 10
                        );
                        return EventResult.INTERRUPT;
                    } else {
                        return EventResult.PASS;
                    }
                });
        RenderGuiLayerEvents.before(RenderGuiLayerEvents.ARMOR_LEVEL).register(
                (Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                    if (minecraft.getCameraEntity() instanceof Player && OverflowingBars.CONFIG.get(
                            ClientConfig.class).armor.allowLayers) {
                        RenderSystem.enableBlend();
                        int guiLeftHeight = ClientAbstractions.INSTANCE.getGuiLeftHeight(minecraft.gui);
                        BarOverlayRenderer.renderArmorLevelBar(minecraft, guiGraphics, guiLeftHeight,
                                OverflowingBars.CONFIG.get(ClientConfig.class).armor.allowCount, false
                        );
                        RenderSystem.disableBlend();
                        BarOverlayRenderer.resetRenderState();
                        if (ChatOffsetHelper.armorRow(minecraft.player)) {
                            ClientAbstractions.INSTANCE.addGuiLeftHeight(minecraft.gui, 10);
                        }
                        return EventResult.INTERRUPT;
                    } else {
                        return EventResult.PASS;
                    }
                });
        RenderGuiEvents.BEFORE.register((Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
            ClientAbstractions.INSTANCE.addGuiLeftHeight(minecraft.gui, OverflowingBars.CONFIG.get(ClientConfig.class).hotbarOffset);
            ClientAbstractions.INSTANCE.addGuiRightHeight(minecraft.gui, OverflowingBars.CONFIG.get(ClientConfig.class).hotbarOffset);
        });
    }

    @Override
    public void onAddResourcePackFinders(PackRepositorySourcesContext context) {
//        context.addRepositorySource(PackResourcesHelper.buildClientPack(OverflowingBars.id("dynamic"),
//                DynamicallyCopiedPackResources.create(), false
//        ));
    }
}
