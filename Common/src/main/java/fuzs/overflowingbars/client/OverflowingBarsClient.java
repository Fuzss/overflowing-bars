package fuzs.overflowingbars.client;

import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.gui.HealthBarRenderer;
import fuzs.overflowingbars.client.handler.GuiLayerHandler;
import fuzs.overflowingbars.config.ClientConfig;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.CustomizeChatPanelCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class OverflowingBarsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientTickEvents.START.register(HealthBarRenderer.INSTANCE::onStartTick);
        CustomizeChatPanelCallback.EVENT.register(GuiLayerHandler::onRenderChatPanel);
        RenderGuiLayerEvents.before(RenderGuiLayerEvents.PLAYER_HEALTH).register(GuiLayerHandler::onRenderPlayerHealth);
        RenderGuiLayerEvents.before(RenderGuiLayerEvents.ARMOR_LEVEL).register(GuiLayerHandler::onRenderArmorLevel);
        // register this properly on NeoForge, so that other mods can interact with the layer
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isFabricLike()) {
            RenderGuiLayerEvents.after(RenderGuiLayerEvents.AIR_LEVEL).register(
                    GuiLayerHandler::onRenderToughnessLevel);
        }
        // TODO move hovering hotbar code
        CustomizeChatPanelCallback.EVENT.register(
                (GuiGraphics guiGraphics, DeltaTracker deltaTracker, MutableInt posX, MutableInt posY) -> {
                    posY.mapInt(value -> value - OverflowingBars.CONFIG.get(ClientConfig.class).hotbarOffset);
                });
        RenderGuiEvents.BEFORE.register((Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
            ClientAbstractions.INSTANCE.addGuiLeftHeight(minecraft.gui,
                    OverflowingBars.CONFIG.get(ClientConfig.class).hotbarOffset
            );
            ClientAbstractions.INSTANCE.addGuiRightHeight(minecraft.gui,
                    OverflowingBars.CONFIG.get(ClientConfig.class).hotbarOffset
            );
        });
    }

    @Override
    public void onAddResourcePackFinders(PackRepositorySourcesContext context) {
//        context.addRepositorySource(PackResourcesHelper.buildClientPack(OverflowingBars.id("dynamic"),
//                DynamicallyCopiedPackResources.create(), false
//        ));
    }
}
