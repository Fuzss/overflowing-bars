package fuzs.overflowingbars.client.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.puzzleslib.api.client.gui.v2.components.GuiGraphicsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class HotbarSpriteHelper {
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace(
            "hud/hotbar_selection");

    public static void blitHotbarSelectionSprite(GuiGraphics guiGraphics) {
        if (Minecraft.getInstance().getCameraEntity() instanceof Player player) {
            int posX = guiGraphics.guiWidth() / 2;
            RenderSystem.enableBlend();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, -90.03F);
            GuiGraphicsHelper.blitTiledSprite(guiGraphics, HOTBAR_SELECTION_SPRITE,
                    posX - 91 - 1 + player.getInventory().selected * 20, guiGraphics.guiHeight() - 22 - 1, 24, 24, 24, 23
            );

            guiGraphics.pose().popPose();
            RenderSystem.disableBlend();
        }
    }
}
