package fuzs.overflowingbars.neoforge.client;

import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.OverflowingBarsClient;
import fuzs.overflowingbars.neoforge.client.handler.NeoForgeGuiOverlayHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.common.NeoForge;

@Mod.EventBusSubscriber(modid = OverflowingBars.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OverflowingBarsNeoForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(OverflowingBars.MOD_ID, OverflowingBarsClient::new);
        registerHandlers();
    }

    private static void registerHandlers() {
        NeoForge.EVENT_BUS.addListener(NeoForgeGuiOverlayHandler::onBeforeRenderGuiOverlay);
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(final RegisterGuiOverlaysEvent evt) {
        evt.registerAbove(VanillaGuiOverlay.MOUNT_HEALTH.id(), "toughness_level", NeoForgeGuiOverlayHandler.TOUGHNESS_LEVEL);
    }
}
