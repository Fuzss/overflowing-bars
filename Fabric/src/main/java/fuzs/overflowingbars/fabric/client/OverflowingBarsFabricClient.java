package fuzs.overflowingbars.fabric.client;

import fuzs.overflowingbars.common.OverflowingBars;
import fuzs.overflowingbars.common.client.OverflowingBarsClient;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import net.fabricmc.api.ClientModInitializer;
import squeek.appleskin.api.event.HUDOverlayEvent;

public class OverflowingBarsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(OverflowingBars.MOD_ID, OverflowingBarsClient::new);
        registerModIntegrations();
    }

    private static void registerModIntegrations() {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("appleskin")) {
            try {
                // just disable this, it's not too useful anyway and would be annoying to get to work properly with the stacked rendering
                HUDOverlayEvent.HealthRestored.EVENT.register((HUDOverlayEvent.HealthRestored healthRestored) -> {
                    healthRestored.isCanceled = true;
                });
            } catch (Throwable throwable) {
                OverflowingBars.LOGGER.warn("Failed to initialize Apple Skin integration!", throwable);
            }
        }
    }
}
