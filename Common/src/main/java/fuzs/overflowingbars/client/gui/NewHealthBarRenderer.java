package fuzs.overflowingbars.client.gui;

import fuzs.overflowingbars.OverflowingBars;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.Identifier;

import java.util.stream.Stream;

public class NewHealthBarRenderer {

    public static Stream<Identifier> getAllTextureLocations() {
        return Stream.of(Gui.HeartType.values())
                .filter(heartType -> heartType != Gui.HeartType.CONTAINER)
                .flatMap(NewHealthBarRenderer::getHeartTypeTextureLocations)
                .map(Identifier::getPath)
                .map(OverflowingBars::id);
    }

    public static Stream<Identifier> getHeartTypeTextureLocations(Gui.HeartType heartType) {
        Stream.Builder<Identifier> builder = Stream.builder();
        builder.add(heartType.full).add(heartType.fullBlinking).add(heartType.half).add(heartType.halfBlinking);
        builder.add(heartType.hardcoreFull)
                .add(heartType.hardcoreFullBlinking)
                .add(heartType.hardcoreHalf)
                .add(heartType.hardcoreHalfBlinking);
        return builder.build();
    }
}
