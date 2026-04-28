package fuzs.overflowingbars.common.client.gui;

import fuzs.overflowingbars.common.OverflowingBars;
import fuzs.overflowingbars.common.config.ClientConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class ArmorBarRenderer {
    private static final ArmorType ARMOR = new ArmorType(OverflowingBars.id("hud/armor/empty"),
            OverflowingBars.id("hud/armor/half"),
            OverflowingBars.id("hud/armor/full"),
            OverflowingBars.id("hud/armor/layer_half"),
            OverflowingBars.id("hud/armor/layer_full"),
            OverflowingBars.id("hud/armor/mixed"));
    private static final ArmorType ARMOR_INVERSE = new ArmorType(OverflowingBars.id("hud/armor/empty"),
            OverflowingBars.id("hud/armor/layer_half"),
            OverflowingBars.id("hud/armor/layer_full"),
            OverflowingBars.id("hud/armor/half"),
            OverflowingBars.id("hud/armor/full"),
            OverflowingBars.id("hud/armor/mixed_mirrored"));
    private static final ArmorType TOUGHNESS = new ArmorType(OverflowingBars.id("hud/toughness/empty"),
            OverflowingBars.id("hud/toughness/half"),
            OverflowingBars.id("hud/toughness/full"),
            OverflowingBars.id("hud/toughness/layer_half"),
            OverflowingBars.id("hud/toughness/layer_full"),
            OverflowingBars.id("hud/toughness/mixed"));
    private static final ArmorType TOUGHNESS_INVERSE = new ArmorType(OverflowingBars.id("hud/toughness/empty"),
            OverflowingBars.id("hud/toughness/layer_half"),
            OverflowingBars.id("hud/toughness/layer_full"),
            OverflowingBars.id("hud/toughness/half"),
            OverflowingBars.id("hud/toughness/full"),
            OverflowingBars.id("hud/toughness/mixed_mirrored"));
    private static final ArmorType TOUGHNESS_MIRRORED = new ArmorType(OverflowingBars.id("hud/toughness/empty"),
            OverflowingBars.id("hud/toughness/half_mirrored"),
            OverflowingBars.id("hud/toughness/full"),
            OverflowingBars.id("hud/toughness/layer_half_mirrored"),
            OverflowingBars.id("hud/toughness/layer_full"),
            OverflowingBars.id("hud/toughness/mixed_mirrored"));
    private static final ArmorType TOUGHNESS_MIRRORED_INVERSE = new ArmorType(OverflowingBars.id("hud/toughness/empty"),
            OverflowingBars.id("hud/toughness/layer_half_mirrored"),
            OverflowingBars.id("hud/toughness/layer_full"),
            OverflowingBars.id("hud/toughness/half_mirrored"),
            OverflowingBars.id("hud/toughness/full"),
            OverflowingBars.id("hud/toughness/mixed"));

    public static void renderArmorBar(GuiGraphicsExtractor guiGraphics, int posX, int posY, Player player) {
        Profiler.get().push(OverflowingBars.id("armor").toString());
        ClientConfig.AbstractArmorRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).armor;
        int armorPoints = player.getArmorValue();
        ArmorType armorType = config.inverseColoring ? ARMOR_INVERSE : ARMOR;
        renderArmorBar(guiGraphics, posX, posY, armorType, armorPoints, true, false, config);
        Profiler.get().pop();
    }

    public static void renderToughnessBar(GuiGraphicsExtractor guiGraphics, int posX, int posY, Player player, boolean leftSide, boolean matchVanilla) {
        Profiler.get().push(OverflowingBars.id("toughness").toString());
        ClientConfig.ToughnessRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).toughness;
        int armorPoints = Mth.floor(player.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        ArmorType armorType;
        if (!matchVanilla && config.inverseColoring) {
            armorType = leftSide ? TOUGHNESS_INVERSE : TOUGHNESS_MIRRORED_INVERSE;
        } else {
            armorType = leftSide ? TOUGHNESS : TOUGHNESS_MIRRORED;
        }

        renderArmorBar(guiGraphics,
                posX,
                posY,
                armorType, armorPoints,
                leftSide,
                matchVanilla,
                config);
        Profiler.get().pop();
    }

    private static void renderArmorBar(GuiGraphicsExtractor guiGraphics, int posX, int posY, ArmorType armorType, int value, boolean leftSide, boolean matchVanilla, ClientConfig.AbstractArmorRowConfig config) {
        if (value <= 0) {
            return;
        }

        boolean skipEmpty = !matchVanilla && config.skipEmptyArmorPoints;
        int lastRowArmorPoints = 0;
        if (!matchVanilla) {
            if (config.colorizeFirstRow || value > 20) {
                lastRowArmorPoints = (value - 1) % 20 + 1;
            }
        }

        for (int currentArmorPoint = 0; currentArmorPoint < 10; ++currentArmorPoint) {
            int startX = posX + (leftSide ? currentArmorPoint * 8 : -currentArmorPoint * 8 - 9);
            if (currentArmorPoint * 2 + 1 < lastRowArmorPoints) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, armorType.layerFull(), startX, posY, 9, 9);
            } else if (currentArmorPoint * 2 + 1 == lastRowArmorPoints) {
                if (value > 20) {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, armorType.mixed(), startX, posY, 9, 9);
                } else {
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, armorType.layerHalf(), startX, posY, 9, 9);
                }
            } else if (currentArmorPoint * 2 + 1 < value) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, armorType.full(), startX, posY, 9, 9);
            } else if (currentArmorPoint * 2 + 1 == value) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, armorType.half(), startX, posY, 9, 9);
            } else if (!skipEmpty && currentArmorPoint * 2 + 1 > value) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, armorType.empty(), startX, posY, 9, 9);
            }
        }
    }

    private record ArmorType(Identifier empty,
                             Identifier half,
                             Identifier full,
                             Identifier layerHalf,
                             Identifier layerFull,
                             Identifier mixed) {

    }
}
