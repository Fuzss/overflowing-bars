package fuzs.overflowingbars.neoforge;

import fuzs.overflowingbars.OverflowingBars;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(OverflowingBars.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class OverflowingBarsNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(OverflowingBars.MOD_ID, OverflowingBars::new);
    }
}
