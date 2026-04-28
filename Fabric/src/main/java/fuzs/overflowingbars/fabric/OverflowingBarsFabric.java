package fuzs.overflowingbars.fabric;

import fuzs.overflowingbars.common.OverflowingBars;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class OverflowingBarsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(OverflowingBars.MOD_ID, OverflowingBars::new);
    }
}
