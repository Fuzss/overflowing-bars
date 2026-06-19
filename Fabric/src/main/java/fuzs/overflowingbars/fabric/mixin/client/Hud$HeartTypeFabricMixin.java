package fuzs.overflowingbars.fabric.mixin.client;

import fuzs.overflowingbars.common.OverflowingBars;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Hud.HeartType.class)
enum Hud$HeartTypeFabricMixin {
    OVERFLOWINGBARS_LAYER(OverflowingBars.id("hud/heart/layer_full"),
            OverflowingBars.id("hud/heart/layer_full_blinking"),
            OverflowingBars.id("hud/heart/layer_half"),
            OverflowingBars.id("hud/heart/layer_half_blinking"),
            OverflowingBars.id("hud/heart/layer_hardcore_full"),
            OverflowingBars.id("hud/heart/layer_hardcore_full_blinking"),
            OverflowingBars.id("hud/heart/layer_hardcore_half"),
            OverflowingBars.id("hud/heart/layer_hardcore_half_blinking"));

    @Shadow
    Hud$HeartTypeFabricMixin(Identifier full, Identifier fullBlinking, Identifier half, Identifier halfBlinking, Identifier hardcoreFull, Identifier hardcoreFullBlinking, Identifier hardcoreHalf, Identifier hardcoreHalfBlinking) {
        // NO-OP
    }
}
