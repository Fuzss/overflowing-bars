package fuzs.overflowingbars.common.client.init;

import fuzs.overflowingbars.common.OverflowingBars;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.Identifier;

import java.util.Locale;
import java.util.function.Function;

public class ModEnumConstants {
    public static final Gui.HeartType LAYER_HEART_TYPE = getEnumConstant(OverflowingBars.id("layer"),
            Gui.HeartType::valueOf);

    public static void bootstrap() {
        // NO-OP
    }

    private static <E extends Enum<E>> E getEnumConstant(Identifier identifier, Function<String, E> valueOfInvoker) {
        return valueOfInvoker.apply(identifier.toDebugFileName().toUpperCase(Locale.ROOT));
    }
}
