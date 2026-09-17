package fuzs.overflowingbars.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.config.ClientConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class HealthBarRenderer {
    public static final HealthBarRenderer INSTANCE = new HealthBarRenderer();

    private final RandomSource random = RandomSource.create();
    private int tickCount;
    private int lastHealth;
    private int displayHealth;
    private long lastHealthTime;
    private long healthBlinkTime;

    public void onStartTick(Minecraft minecraft) {
        this.tickCount++;
    }

    public void renderPlayerHealth(GuiGraphics guiGraphics, int posX, int posY, Player player, ProfilerFiller profiler) {
        profiler.push("health");
        BarOverlayRenderer.resetRenderState();
        RenderSystem.enableBlend();
        int currentHealth = Mth.ceil(player.getHealth());
        boolean blink = this.healthBlinkTime > (long) this.tickCount && (this.healthBlinkTime - (long) this.tickCount) / 3L % 2L == 1L;
        long millis = Util.getMillis();
        if (currentHealth < this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = millis;
            this.healthBlinkTime = this.tickCount + 20;
        } else if (currentHealth > this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = millis;
            this.healthBlinkTime = this.tickCount + 10;
        }

        if (millis - this.lastHealthTime > 1000L) {
            this.displayHealth = currentHealth;
            this.lastHealthTime = millis;
        }

        this.lastHealth = currentHealth;
        int displayHealth = this.displayHealth;
        this.random.setSeed(this.tickCount * 312871);
        float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), (float) Math.max(displayHealth, currentHealth));
        int currentAbsorption = Mth.ceil(player.getAbsorptionAmount());
        int heartOffsetByRegen = -1;
        if (player.hasEffect(MobEffects.REGENERATION)) {
            heartOffsetByRegen = this.tickCount % Mth.ceil(Math.min(20.0F, maxHealth) + 5.0F);
        }
        this.renderHearts(guiGraphics, player, posX, posY, heartOffsetByRegen, maxHealth, currentHealth, displayHealth, currentAbsorption, blink);
        RenderSystem.disableBlend();
        profiler.pop();
    }

    private void renderHearts(GuiGraphics guiGraphics, Player player, int posX, int posY, int heartOffsetByRegen, float maxHealth, int currentHealth, int displayHealth, int currentAbsorptionHealth, boolean blink) {
        boolean hardcore = player.level().getLevelData().isHardcore();
        // The number of heart slots to be shown on the hotbar (maximum of 10).
        int normalHearts = Math.min(10, Mth.ceil((double) maxHealth / 2.0));
        // Absorption hearts are capped to completely filling a second row (?)
        int maxAbsorptionHearts = 20 - normalHearts;
        int absorptionHearts = Math.min(20 - normalHearts, Mth.ceil((double) currentAbsorptionHealth / 2.0));

        for (int currentHeart = 0; currentHeart < normalHearts + absorptionHearts; ++currentHeart) {

            int currentPosX = posX + (currentHeart % 10) * 8;
            int currentPosY = posY - (currentHeart / 10) * 10;

            // q: is this how it works? I thought absorption did not affect the shaking hearts animation. I might be stupid though
            if (currentHealth + currentAbsorptionHealth <= 4) {
                currentPosY += this.random.nextInt(2);
            }

            // Jiggle one specific heart of the normal row for the regen animation
            if (currentHeart < normalHearts && heartOffsetByRegen == currentHeart) {
                currentPosY -= 2;
            }

            guiGraphics.pose().pushPose();

            // renders the black heart outline and background (only visible for half hearts)
            ModHeartType.CONTAINER.renderHeart(guiGraphics, currentPosX, currentPosY, blink, false, hardcore);
            // then the first call to renderHeart renders the heart from the layer below in case the current layer heart is just half a heart
            // the second call renders the actual heart from the current layer
            if (currentHeart >= normalHearts) {
                int currentAbsorption = currentHeart * 2 - normalHearts * 2;
                if (currentAbsorption < currentAbsorptionHealth) {
                    int maxAbsorptionHealth = maxAbsorptionHearts * 2;
                    boolean halfHeart = currentAbsorption + 1 == currentAbsorptionHealth % maxAbsorptionHealth;
                    boolean notBottomLayer = currentAbsorptionHealth > maxAbsorptionHealth && currentAbsorption + 1 <= (currentAbsorptionHealth - 1) % maxAbsorptionHealth + 1;
                    boolean inSecondPhase = currentHeart * 2 + 1 > (currentAbsorptionHealth - 1) % 20 + 1;
                    int gradient = (currentAbsorptionHealth / 20) + inSecondPhase && notBottomLayer ? 0 : 1;
                    if (halfHeart && notBottomLayer) {
                        ModHeartType.forPlayer(player, true, gradient - 1).renderHeart(guiGraphics, currentPosX, currentPosY, false, false, hardcore);
                    }
                    ModHeartType.forPlayer(player, true, gradient).renderHeart(guiGraphics, currentPosX, currentPosY, false, halfHeart, hardcore);
                }
            }

            if (blink && currentHeart * 2 < Math.min(20, displayHealth)) {
                boolean halfHeart = currentHeart * 2 + 1 == (displayHealth - 1) % 20 + 1;
                boolean notBottomLayer = displayHealth > 20 && currentHeart * 2 + 1 <= (displayHealth - 1) % 20 + 1;
                if (halfHeart && notBottomLayer) {
                    ModHeartType.forPlayer(player, false, gradient - 1).renderHeart(guiGraphics, currentPosX, currentPosY, true, false, hardcore);
                }
                boolean inSecondPhase = currentHeart * 2 + 1 > (displayHealth - 1) % 20 + 1;
                int gradient = (displayHealth / 20) + inSecondPhase && notBottomLayer ? 0 : 1;
                if (
                    OverflowingBars.CONFIG.get(ClientConfig.class).health.colorizeFirstRow
                 && OverflowingBars.CONFIG.get(ClientConfig.class).health.colorizeInGradient
                 && currentHeart * 2 + 1 <= (displayHealth - 1) % 20 + 1
                ) {
                    gradient = 2;
                }
                ModHeartType heartType = ModHeartType.forPlayer(player, false, gradient);
                heartType.renderHeart(guiGraphics, currentPosX, currentPosY, true, halfHeart, hardcore);
            }

            if (currentHeart * 2 < Math.min(20, currentHealth)) {
                boolean halfHeart = currentHeart * 2 + 1 == (currentHealth - 1) % 20 + 1;
                boolean notBottomLayer = currentHealth > 20 && currentHeart * 2 + 1 <= (currentHealth - 1) % 20 + 1;
                if (halfHeart && notBottomLayer) {
                    ModHeartType.forPlayer(player, false, false).renderHeart(guiGraphics, currentPosX, currentPosY, false, false, hardcore);
                }
                boolean inSecondPhase = currentHeart * 2 + 1 > (currentHealth - 1) % 20 + 1;
                int gradient = (currentHealth / 20) + inSecondPhase && notBottomLayer ? 0 : 1;
                if (
                    OverflowingBars.CONFIG.get(ClientConfig.class).health.colorizeFirstRow
                 && OverflowingBars.CONFIG.get(ClientConfig.class).health.colorizeInGradient
                 && currentHeart * 2 + 1 <= (currentHealth - 1) % 20 + 1
                ) {
                    gradient = 2;
                }
                ModHeartType heartType = ModHeartType.forPlayer(player, false, gradient);
                heartType.renderHeart(guiGraphics, currentPosX, currentPosY, false, halfHeart, hardcore);
            }

            guiGraphics.pose().popPose();
        }
    }

    enum ModHeartType {
        CONTAINER(Gui.HeartType.CONTAINER),
        NORMAL(Gui.HeartType.NORMAL),
        POISONED(Gui.HeartType.POISIONED),
        WITHERED(Gui.HeartType.WITHERED),
        ABSORBING(Gui.HeartType.ABSORBING),
        FROZEN(Gui.HeartType.FROZEN),
        NORMAL2(0, 3, 4, BarOverlayRenderer.OVERFLOWING_ICONS_LOCATION, true);
        NORMAL3(0, 5, 6, BarOverlayRenderer.OVERFLOWING_ICONS_LOCATION, true);
        NORMAL4(0, 7, 8, BarOverlayRenderer.OVERFLOWING_ICONS_LOCATION, true);
        NORMAL5(0, 9, 10, BarOverlayRenderer.OVERFLOWING_ICONS_LOCATION, true);
        NORMAL6(4, 3, 4, BarOverlayRenderer.OVERFLOWING_ICONS_LOCATION, true);
        NORMAL7(4, 5, 6, BarOverlayRenderer.OVERFLOWING_ICONS_LOCATION, true);
        NORMAL8(4, 7, 8, BarOverlayRenderer.OVERFLOWING_ICONS_LOCATION, true);

        @Nullable
        private final Gui.HeartType heartType;
        private final int textureIndexX;
        private final int textureIndexY;
        private final int hardcoreIndexY;
        private final ResourceLocation textureSheet;
        private final boolean canBlink;

        ModHeartType(Gui.HeartType heartType) {
            this.heartType = heartType;
            this.textureIndexX = -1;
            this.textureIndexY = -1;
            this.hardcoreIndexY = -1;
            this.textureSheet = null;
            this.canBlink = false;
        }

        ModHeartType(int textureIndexX, int textureIndexY, int hardcoreIndexY, ResourceLocation textureSheet, boolean blink) {
            this.heartType = null;
            this.textureIndexX = textureIndexX;
            this.textureIndexY = textureIndexY;
            this.hardcoreIndexY = hardcoreIndexY;
            this.textureSheet = textureSheet;
            this.canBlink = blink;
        }

        public void renderHeart(GuiGraphics guiGraphics, int posX, int posY, boolean blinking, boolean halfHeart, boolean hardcore) {
            // same offset as font shadow to avoid issues with optimization mods batching drawn layers together
            guiGraphics.pose().translate(0.0F, 0.0F, 0.03F);
            if (this.heartType != null) {
                ResourceLocation resourceLocation = this.heartType.getSprite(hardcore, halfHeart, blinking);
                guiGraphics.blitSprite(resourceLocation, posX, posY, 9, 9);
            } else {
                guiGraphics.blit(this.textureSheet, posX, posY, this.getX(halfHeart, blinking), this.getY(hardcore), 9, 9);
            }
        }

        public int getX(boolean halfHeart, boolean blinking) {
            int i;
            if (this == CONTAINER) {
                i = blinking ? 1 : 0;
            } else {
                int j = halfHeart ? 1 : 0;
                int k = this.canBlink && blinking ? 2 : 0;
                i = j + k;
            }

            return (this.isGradient() ? 0 : 16) + (this.textureIndexX * 2 + i) * 9;
        }

        public int getY(boolean hardcore) {
            return (hardcore ? this.hardcoreIndexY : this.textureIndexY) * 9;
        }

        public static ModHeartType ofGradient(int gradient) {
            switch (gradient) {
                case 1:
                    return NORMAL;
                case 2:
                    return NORMAL2;
                case 3:
                    return NORMAL3;
                case 4:
                    return NORMAL4;
                case 5:
                    return NORMAL5;
                case 6:
                    return NORMAL6;
                case 7:
                    return NORMAL7;
                case 8:
                    return NORMAL8;
            }
        }

        public static boolean isGradient() {
            return this == NORMAL2 || this == NORMAL3 || this == NORMAL4 || this == NORMAL5 || this == NORMAL6;
        }

        public static ModHeartType forPlayer(Player player, boolean absorbing, int gradient) {
            if (player.hasEffect(MobEffects.WITHER)) {
                return WITHERED;
            } else if (player.hasEffect(MobEffects.POISON)) {
                return POISONED;
            } else if (player.isFullyFrozen()) {
                return FROZEN;
            } else {
                int gradient_calc = ((gradient - 1) + 8) % 8 + 1;
                boolean inverse = OverflowingBars.CONFIG.get(ClientConfig.class).health.inverseColoring;
                boolean use_gradient = OverflowingBars.CONFIG.get(ClientConfig.class).health.colorizeInGradient;
                if (inverse) {
                    gradient_calc = 9 - gradient_calc;
                }
                if (!use_gradient) {
                    if (gradient_calc >= 2) {
                        gradient_calc = 2;
                    }
                }
                return absorbing ? ABSORBING : ofGradient(gradient_calc);
            }
        }
    }
}
