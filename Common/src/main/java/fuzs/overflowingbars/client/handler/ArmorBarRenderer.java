package fuzs.overflowingbars.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.config.ClientConfig;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class ArmorBarRenderer {

    public static void renderArmorBar(PoseStack poseStack, int posX, int posY, Player player, ProfilerFiller profiler, boolean unmodified) {
        profiler.push("armor");
        ClientConfig.ArmorRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).armor;
        int armorPoints = player.getArmorValue();
        renderArmorBar(poseStack, posX, posY, 18, armorPoints, true, unmodified, config);
        profiler.pop();
    }

    public static void renderToughnessBar(PoseStack poseStack, int posX, int posY, Player player, ProfilerFiller profiler, boolean left, boolean unmodified) {
        profiler.push("toughness");
        ClientConfig.ToughnessRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).toughness;
        int armorPoints = Mth.floor(player.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        renderArmorBar(poseStack, posX, posY, left ? 9 : 0, armorPoints, left, unmodified, config);
        profiler.pop();
    }

    public static void renderArmorBar(PoseStack poseStack, int posX, int posY, int vOffset, int armorPoints, boolean left, boolean unmodified, ClientConfig.ArmorRowConfig config) {
        if (armorPoints <= 0) return;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        boolean inverse = !unmodified && config.inverseColoring;
        boolean skip = !unmodified && config.skipEmptyArmorPoints;
        int lastRowArmorPoints = 0;
        if (!unmodified) {
            if (config.colorizeFirstRow || armorPoints > 20) {
                lastRowArmorPoints = (armorPoints - 1) % 20 + 1;
            }
        }
        for (int currentArmorPoint = 0; currentArmorPoint < 10; ++currentArmorPoint) {
            int startX = posX + (left ? currentArmorPoint * 8 : -currentArmorPoint * 8 - 9);
            if (currentArmorPoint * 2 + 1 < lastRowArmorPoints) {
                RenderSystem.setShaderTexture(0, BarOverlayRenderer.OVERFLOWING_ICONS_LOCATION);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                GuiComponent.blit(poseStack, startX, posY, inverse ? 18 : 36, vOffset, 9, 9, 256, 256);
            } else if (currentArmorPoint * 2 + 1 == lastRowArmorPoints) {
                if (armorPoints > 20) {
                    RenderSystem.setShaderTexture(0, BarOverlayRenderer.OVERFLOWING_ICONS_LOCATION);
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    GuiComponent.blit(poseStack, startX, posY, inverse ? 54 : 27, vOffset, 9, 9, 256, 256);
                } else {
                    RenderSystem.setShaderTexture(0, BarOverlayRenderer.OVERFLOWING_ICONS_LOCATION);
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    GuiComponent.blit(poseStack, startX, posY, inverse ? 9 : 45, vOffset, 9, 9, 256, 256);
                }
            } else if (currentArmorPoint * 2 + 1 < armorPoints) {
                RenderSystem.setShaderTexture(0, BarOverlayRenderer.OVERFLOWING_ICONS_LOCATION);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                GuiComponent.blit(poseStack, startX, posY, inverse ? 36 : 18, vOffset, 9, 9, 256, 256);
            } else if (currentArmorPoint * 2 + 1 == armorPoints) {
                RenderSystem.setShaderTexture(0, BarOverlayRenderer.OVERFLOWING_ICONS_LOCATION);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                GuiComponent.blit(poseStack, startX, posY, inverse ? 45 : 9, vOffset, 9, 9, 256, 256);
            } else if (!skip && currentArmorPoint * 2 + 1 > armorPoints) {
                RenderSystem.setShaderTexture(0, BarOverlayRenderer.OVERFLOWING_ICONS_LOCATION);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                GuiComponent.blit(poseStack, startX, posY, 0, vOffset, 9, 9, 256, 256);
            }
        }
    }
}
