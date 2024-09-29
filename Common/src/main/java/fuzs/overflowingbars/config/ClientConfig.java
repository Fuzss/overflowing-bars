package fuzs.overflowingbars.config;

import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientConfig implements ConfigCore {
    static final String CATEGORY_GENERAL = "general";

    @Config
    public IconRowConfig health = new IconRowConfig();
    @Config
    public ArmorRowConfig armor = new ArmorRowConfig();
    @Config
    public ToughnessRowConfig toughness = new ToughnessRowConfig();
    @Config
    public RowCountConfig rowCount = new RowCountConfig();
    @Config(category = CATEGORY_GENERAL, description = "Move chat messages above armor/absorption bar.")
    public boolean moveChatAboveArmor = true;
    @Config(category = CATEGORY_GENERAL, description = "Move the experience level display above the experience bar.")
    public boolean moveExperienceAboveBar = true;
    @Config(category = CATEGORY_GENERAL, description = "Height offset for the hotbar from the screen bottom.")
    @Config.IntRange(min = 0, max = 64)
    public int hotbarOffset = 2;

    private ModConfigSpec.ConfigValue<List<? extends String>> hotbarGuiLayersValue;
    public Set<ResourceLocation> hotbarGuiLayers = Collections.emptySet();

    @Override
    public void addToBuilder(ModConfigSpec.Builder builder, ValueCallback callback) {
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isForgeLike()) {
            // We do not need to include {@link VanillaGuiLayers#SELECTED_ITEM_NAME} &amp; {@link VanillaGuiLayers#OVERLAY_MESSAGE},
            // both get their render height from {@link Gui#leftHeight} &amp; {@link Gui#rightHeight}.
            List<String> defaultValue = Stream.of(RenderGuiLayerEvents.HOTBAR, RenderGuiLayerEvents.JUMP_METER,
                    RenderGuiLayerEvents.EXPERIENCE_BAR, RenderGuiLayerEvents.SPECTATOR_TOOLTIP,
                    RenderGuiLayerEvents.EXPERIENCE_LEVEL
            ).map(ResourceLocation::toString).collect(Collectors.toList());
            this.hotbarGuiLayersValue = builder.comment(
                    "Defines a set of gui layers that should be shifted together with the hotbar.").defineList(
                    "hotbar_gui_layers", defaultValue, () -> "",
                    o -> o instanceof String s && ResourceLocationHelper.tryParse(s) != null
            );
        }
    }

    @Override
    public void afterConfigReload() {
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isForgeLike()) {
            this.hotbarGuiLayers = this.hotbarGuiLayersValue.get().stream().map(ResourceLocationHelper::parse).collect(
                    ImmutableSet.toImmutableSet());
        }
    }

    public static class IconRowConfig implements ConfigCore {
        @Config(description = "Add layers to this bar. When disabled any modifications to the bar from this mod will be turned off.")
        public boolean allowLayers = true;
        @Config(description = "Render row count to indicate total amount of rows since not all may be visible at once due to the stacked rendering.")
        public boolean allowCount = true;
        @Config(description = "Show colorful icons on the front row, not just on all subsequent rows.")
        public boolean colorizeFirstRow = false;
        @Config(description = "Use vanilla's icons on all front rows, use custom colored icons on the background row.")
        public boolean inverseColoring = false;
        @Config(description = "Shift the bar up or down by specified number of icon rows. Allows for better mod compatibility.")
        @Config.IntRange(min = -5, max = 5)
        public int manualRowShift = 0;

        public int manualRowShift() {
            return this.manualRowShift * 10;
        }
    }

    public static class ArmorRowConfig extends IconRowConfig {
        @Config(description = "Don't draw empty armor points, this will make the armor bar potentially shorter.")
        public boolean skipEmptyArmorPoints = true;
    }

    public static class ToughnessRowConfig extends ArmorRowConfig {
        @Config(
                description = {
                        "Render a separate armor bar for the armor toughness attribute (from diamond and netherite armor).",
                        "Having only this option active will make the toughness bar behave just like vanilla's armor bar without any colorful stacking or so."
                }
        )
        public boolean armorToughnessBar = true;
        @Config(description = "Render the toughness bar on the left side above the hotbar (where health and armor is rendered).")
        public boolean leftSide = false;
    }

    public static class RowCountConfig implements ConfigCore {
        @Config(description = "Color of row count, use any chat formatting color value.")
        @Config.AllowedValues(
                values = {
                        "BLACK",
                        "DARK_BLUE",
                        "DARK_GREEN",
                        "DARK_AQUA",
                        "DARK_RED",
                        "DARK_PURPLE",
                        "GOLD",
                        "GRAY",
                        "DARK_GRAY",
                        "BLUE",
                        "GREEN",
                        "AQUA",
                        "RED",
                        "LIGHT_PURPLE",
                        "YELLOW",
                        "WHITE"
                }
        )
        public ChatFormatting rowCountColor = ChatFormatting.WHITE;
        @Config(description = "Force drawing row count using the font renderer, will make numbers display larger.")
        public boolean forceFontRenderer = false;
        @Config(description = "Only include completely filled rows for the row count.")
        public boolean countFullRowsOnly = false;
        @Config(description = "Show row count also when only one row is present.")
        public boolean alwaysRenderRowCount = false;
        @Config(description = "Render an 'x' together with the row count number.")
        public boolean rowCountX = true;
    }
}
