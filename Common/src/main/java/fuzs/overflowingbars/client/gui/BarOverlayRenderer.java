package fuzs.overflowingbars.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class BarOverlayRenderer {
    static final ResourceLocation OVERFLOWING_ICONS_LOCATION = OverflowingBars.id("textures/gui/icons.png");

    public static void renderHealthLevelBars(Minecraft minecraft, GuiGraphics guiGraphics, int leftHeight, boolean rowCount) {
        if (minecraft.getCameraEntity() instanceof Player player) {
            int posX = guiGraphics.guiWidth() / 2 - 91 + OverflowingBars.CONFIG.get(ClientConfig.class).health.manualRowShiftX;
            int posY = guiGraphics.guiHeight() - leftHeight + OverflowingBars.CONFIG.get(ClientConfig.class).health.manualRowShiftY;
            HealthBarRenderer.INSTANCE.renderPlayerHealth(guiGraphics, posX, posY, player, minecraft.getProfiler());
            if (rowCount) {
                int allHearts = Mth.ceil(player.getHealth());
                RowCountRenderer.drawBarRowCount(guiGraphics, posX - 2, posY, allHearts, true, minecraft.font);
                int maxAbsorption = (20 - Mth.ceil(Math.min(20, allHearts) / 2.0F)) * 2;
                RowCountRenderer.drawBarRowCount(guiGraphics, posX - 2, posY - 10, Mth.ceil(player.getAbsorptionAmount()), true, maxAbsorption, minecraft.font);
            }
        }
    }

    public static void renderArmorLevelBar(Minecraft minecraft, GuiGraphics guiGraphics, int leftHeight, boolean rowCount, boolean unmodified) {
        if (minecraft.getCameraEntity() instanceof Player player) {
            int posX = guiGraphics.guiWidth() / 2 - 91 + OverflowingBars.CONFIG.get(ClientConfig.class).armor.manualRowShiftX;
            int posY = guiGraphics.guiHeight() - leftHeight + OverflowingBars.CONFIG.get(ClientConfig.class).armor.manualRowShiftY;
            ArmorBarRenderer.renderArmorBar(guiGraphics, posX, posY, player, minecraft.getProfiler(), unmodified);
            if (rowCount && !unmodified) {
                RowCountRenderer.drawBarRowCount(guiGraphics, posX - 2, posY, player.getArmorValue(), true,
                        minecraft.font
                );
            }
        }
    }

    public static void renderToughnessLevelBar(Minecraft minecraft, GuiGraphics guiGraphics, int guiHeight, boolean rowCount, boolean leftSide, boolean unmodified) {
        if (minecraft.getCameraEntity() instanceof Player player) {
            int posX = guiGraphics.guiWidth() / 2 + (leftSide ? -91 : 91) + OverflowingBars.CONFIG.get(ClientConfig.class).toughness.manualRowShiftX;
            int posY = guiGraphics.guiHeight() - guiHeight + OverflowingBars.CONFIG.get(ClientConfig.class).toughness.manualRowShiftY;
            ArmorBarRenderer.renderToughnessBar(guiGraphics, posX, posY, player, minecraft.getProfiler(), leftSide,
                    unmodified
            );
            if (rowCount && !unmodified) {
                int toughnessValue = Mth.floor(player.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
                RowCountRenderer.drawBarRowCount(guiGraphics, posX - 2, posY, toughnessValue, leftSide,
                        minecraft.font
                );
            }
        }
    }

    public static void resetRenderState() {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
    }
}
