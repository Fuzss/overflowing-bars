package fuzs.overflowingbars.fabric.mixin.client;

import fuzs.overflowingbars.common.OverflowingBars;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Gui.HeartType.class)
enum Gui$HeartTypeFabricMixin {
    OVERFLOWINGBARS_LAYER(OverflowingBars.id("hud/heart/layer_full"),
            OverflowingBars.id("hud/heart/layer_full_blinking"),
            OverflowingBars.id("hud/heart/layer_half"),
            OverflowingBars.id("hud/heart/layer_half_blinking"),
            OverflowingBars.id("hud/heart/layer_hardcore_full"),
            OverflowingBars.id("hud/heart/layer_hardcore_full_blinking"),
            OverflowingBars.id("hud/heart/layer_hardcore_half"),
            OverflowingBars.id("hud/heart/layer_hardcore_half_blinking"));

    @Shadow
    Gui$HeartTypeFabricMixin(Identifier full, Identifier fullBlinking, Identifier half, Identifier halfBlinking, Identifier hardcoreFull, Identifier hardcoreFullBlinking, Identifier hardcoreHalf, Identifier hardcoreHalfBlinking) {
        // NO-OP
    }
}
