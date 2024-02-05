package fuzs.overflowingbars.client;

import com.mojang.blaze3d.platform.Window;
import fuzs.overflowingbars.client.handler.HealthBarRenderer;
import fuzs.overflowingbars.client.helper.ChatOffsetHelper;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.CustomizeChatPanelCallback;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.client.gui.GuiGraphics;

public class OverflowingBarsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        ClientTickEvents.START.register(HealthBarRenderer.INSTANCE::onStartTick);
        CustomizeChatPanelCallback.EVENT.register((Window window, GuiGraphics guiGraphics, float partialTick, MutableInt posX, MutableInt posY) -> {
            posY.mapInt(v -> v - ChatOffsetHelper.getChatOffsetY());
        });
    }
}
