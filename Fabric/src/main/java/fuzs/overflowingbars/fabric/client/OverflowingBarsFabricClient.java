package fuzs.overflowingbars.fabric.client;

import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.OverflowingBarsClient;
import fuzs.overflowingbars.fabric.integration.appleskin.AppleSkinIntegration;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.fabricmc.api.ClientModInitializer;

public class OverflowingBarsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(OverflowingBars.MOD_ID, OverflowingBarsClient::new);
        registerIntegration();
    }

    private static void registerIntegration() {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("appleskin")) {
            AppleSkinIntegration.init();
        }
    }
}
