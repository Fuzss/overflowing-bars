package fuzs.overflowingbars.mixin.client;

import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.helper.ChatOffsetHelper;
import fuzs.overflowingbars.config.ClientConfig;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatComponent.class)
abstract class ChatComponentMixin {

    @ModifyVariable(method = "screenToChatY", at = @At(value = "LOAD", ordinal = 0), ordinal = 0, argsOnly = true)
    private double screenToChatY(double mouseY) {
        if (!OverflowingBars.CONFIG.get(ClientConfig.class).moveChatAboveArmor) return mouseY;
        return mouseY + ChatOffsetHelper.getChatOffsetY();
    }
}
