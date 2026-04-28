package fuzs.overflowingbars.neoforge.client;

import fuzs.overflowingbars.common.OverflowingBars;
import fuzs.overflowingbars.common.client.OverflowingBarsClient;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = OverflowingBars.MOD_ID, dist = Dist.CLIENT)
public class OverflowingBarsNeoForgeClient {

    public OverflowingBarsNeoForgeClient(ModContainer modContainer) {
        ClientModConstructor.construct(OverflowingBars.MOD_ID, OverflowingBarsClient::new);
        registerModIntegrations();
    }

    private static void registerModIntegrations() {
        if (ModLoaderEnvironment.INSTANCE.isModLoaded("appleskin")) {
            // just disable this, it's not too useful anyway and would be annoying to get to work properly with the stacked rendering
            Identifier identifier = Identifier.parse("appleskin:health_restored");
            NeoForge.EVENT_BUS.addListener((final RenderGuiLayerEvent.Pre evt) -> {
                if (evt.getName().equals(identifier)) {
                    evt.setCanceled(true);
                }
            });
        }
    }
}
