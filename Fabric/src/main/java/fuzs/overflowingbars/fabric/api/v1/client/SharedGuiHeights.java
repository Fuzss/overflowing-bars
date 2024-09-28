package fuzs.overflowingbars.fabric.api.v1.client;

public class SharedGuiHeights {
    /**
     * A key to be used with {@link net.fabricmc.loader.api.ObjectShare} (obtainable via {@link net.fabricmc.loader.api.FabricLoader#getObjectShare()}),
     * contains current height of right icons/bars rendered above hotbar as {@link org.apache.commons.lang3.mutable.MutableInt}. Always provided during every gui render frame by the end of Gui::renderPlayerHealth.
     * <p>The value is the same as ForgeGui#leftHeight on Forge (use that value on Forge for proper mod support, no special trickery required), meaning it's the offset from the screen bottom (including the hotbar height of 39 pixels).
     * <p>When using the value make sure to update the mutable instance by adding the height of your display, there is no need to replace the object in the object share.
     * <p>You do not need a hard dependency on this mod to take advantage of the functionality, simply copying the key is enough (that's the whole point of the object share system). The key is only contained in an api package for better visibility.
     * <p>Example usage:
     * <pre>{@code
     * Optional<MutableInt> leftHeight = FabricLoader.getInstance().getObjectShare().get("overflowingbars:left_height") instanceof MutableInt height ? Optional.of(height) : Optional.empty();
     * leftHeight.map(MutableInt::intValue).orElseGet(() -> ...);
     * leftHeight.ifPresent(mutableInt -> mutableInt.add(...));
     * }</pre>
     */
    public static final String OBJECT_SHARE_LEFT_HEIGHT_KEY = "overflowingbars:left_height";
    /**
     * A key to be used with {@link net.fabricmc.loader.api.ObjectShare} (obtainable via {@link net.fabricmc.loader.api.FabricLoader#getObjectShare()}),
     * contains current height of right icons/bars rendered above hotbar as {@link org.apache.commons.lang3.mutable.MutableInt}. Always provided during every gui render frame by the end of Gui::renderPlayerHealth.
     * <p>The value is the same as ForgeGui#rightHeight on Forge (use that value on Forge for proper mod support, no special trickery required), meaning it's the offset from the screen bottom (including the hotbar height of 39 pixels).
     * <p>When using the value make sure to update the mutable instance by adding the height of your display, there is no need to replace the object in the object share.
     * <p>You do not need a hard dependency on this mod to take advantage of the functionality, simply copying the key is enough (that's the whole point of the object share system). The key is only contained in an api package for better visibility.
     * <p>Example usage:
     * <pre>{@code
     * Optional<MutableInt> rightHeight = FabricLoader.getInstance().getObjectShare().get("overflowingbars:right_height") instanceof MutableInt height ? Optional.of(height) : Optional.empty();
     * rightHeight.map(MutableInt::intValue).orElseGet(() -> ...);
     * rightHeight.ifPresent(mutableInt -> mutableInt.add(...));
     * }</pre>
     */
    public static final String OBJECT_SHARE_RIGHT_HEIGHT_KEY = "overflowingbars:right_height";
}
