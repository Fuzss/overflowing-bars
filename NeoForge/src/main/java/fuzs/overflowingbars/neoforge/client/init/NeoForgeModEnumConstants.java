package fuzs.overflowingbars.neoforge.client.init;

import fuzs.overflowingbars.common.OverflowingBars;
import net.minecraft.client.gui.Gui;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

/**
 * Accessed by {@code enumextensions.json}.
 */
@SuppressWarnings("unused")
public class NeoForgeModEnumConstants {
    public static final EnumProxy<Gui.HeartType> LAYER_HEART_TYPE = new EnumProxy<>(Gui.HeartType.class,
            OverflowingBars.id("hud/heart/layer_full"),
            OverflowingBars.id("hud/heart/layer_full_blinking"),
            OverflowingBars.id("hud/heart/layer_half"),
            OverflowingBars.id("hud/heart/layer_half_blinking"),
            OverflowingBars.id("hud/heart/layer_hardcore_full"),
            OverflowingBars.id("hud/heart/layer_hardcore_full_blinking"),
            OverflowingBars.id("hud/heart/layer_hardcore_half"),
            OverflowingBars.id("hud/heart/layer_hardcore_half_blinking"));
}
