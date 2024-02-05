package fuzs.overflowingbars.client;

import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.handler.GuiOverlayHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = OverflowingBars.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OverflowingBarsForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(OverflowingBars.MOD_ID, OverflowingBarsClient::new);
        registerHandlers();
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener(GuiOverlayHandler::onBeforeRenderGuiOverlay);
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(final RegisterGuiOverlaysEvent evt) {
        evt.registerAbove(VanillaGuiOverlay.MOUNT_HEALTH.id(), "toughness_level", GuiOverlayHandler.TOUGHNESS_LEVEL);
    }
}
