package fuzs.overflowingbars.neoforge.client;

import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.OverflowingBarsClient;
import fuzs.overflowingbars.config.ClientConfig;
import fuzs.overflowingbars.neoforge.client.handler.GuiLayersHandler;
import fuzs.overflowingbars.neoforge.client.handler.HoveringHotbarHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = OverflowingBars.MOD_ID, dist = Dist.CLIENT)
public class OverflowingBarsNeoForgeClient {

    public OverflowingBarsNeoForgeClient(ModContainer modContainer) {
        ClientModConstructor.construct(OverflowingBars.MOD_ID, OverflowingBarsClient::new);
        registerLoadingHandlers(modContainer.getEventBus());
        registerEventHandlers(NeoForge.EVENT_BUS);
        registerModIntegrations();
    }

    private static void registerLoadingHandlers(IEventBus eventBus) {
        eventBus.addListener((final RegisterGuiLayersEvent evt) -> {
            evt.registerAbove(VanillaGuiLayers.VEHICLE_HEALTH, OverflowingBars.id("toughness_level"),
                    GuiLayersHandler.TOUGHNESS_LEVEL
            );
        });
    }

    private static void registerEventHandlers(IEventBus eventBus) {
//        eventBus.addListener(GuiLayersHandler::onBeforeRenderGuiOverlay);
        eventBus.addListener(HoveringHotbarHandler::onBeforeRenderGui);
        eventBus.addListener(HoveringHotbarHandler::onAfterRenderGui);
        eventBus.addListener(HoveringHotbarHandler::onBeforeRenderGuiLayer);
//        eventBus.addListener((final CustomizeGuiOverlayEvent.Chat evt) -> {
//            evt.setPosY(evt.getPosY() - OverflowingBars.CONFIG.get(ClientConfig.class).hotbarOffset);
//        });
    }

    private static void registerModIntegrations() {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("appleskin")) {
            // just disable this, it's not too useful anyway and would be annoying to get to work properly with the stacked rendering
            ResourceLocation resourceLocation = ResourceLocationHelper.parse("appleskin:health_restored");
            NeoForge.EVENT_BUS.addListener((final RenderGuiLayerEvent.Pre evt) -> {
                if (evt.getName().equals(resourceLocation)) {
                    evt.setCanceled(true);
                }
            });
        }
    }
}
