package fuzs.overflowingbars.integration.appleskin;

import fuzs.overflowingbars.OverflowingBars;
import squeek.appleskin.api.event.HUDOverlayEvent;

/**
 * not necessary on Forge as Apple Skin's rendering is tied to the vanilla health overlay which we cancel over there
 */
public class AppleSkinIntegration {

    public static void init() {
        try {
            // just disable this, it's not too useful anyway and would be annoying to get to work properly with the stacked rendering
            HUDOverlayEvent.HealthRestored.EVENT.register(healthRestored -> healthRestored.isCanceled = true);
        } catch (Throwable throwable) {
            OverflowingBars.LOGGER.warn("Failed to initialize Apple Skin integration", throwable);
        }
    }
}
