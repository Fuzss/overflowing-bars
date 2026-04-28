package fuzs.overflowingbars.common.client.gui;

import fuzs.overflowingbars.common.OverflowingBars;
import fuzs.overflowingbars.common.client.init.ModEnumConstants;
import fuzs.overflowingbars.common.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

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

    public void renderPlayerHealth(GuiGraphicsExtractor guiGraphics, int posX, int posY, Player player) {
        Profiler.get().push(OverflowingBars.id("health").toString());
        int currentHealth = Mth.ceil(player.getHealth());
        boolean blink = this.healthBlinkTime > (long) this.tickCount
                && (this.healthBlinkTime - (long) this.tickCount) / 3L % 2L == 1L;
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
        this.random.setSeed(this.tickCount * 312871L);
        float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH),
                (float) Math.max(displayHealth, currentHealth));
        int currentAbsorption = Mth.ceil(player.getAbsorptionAmount());
        int heartOffsetByRegen = -1;
        if (player.hasEffect(MobEffects.REGENERATION)) {
            heartOffsetByRegen = this.tickCount % Mth.ceil(Math.min(20.0F, maxHealth) + 5.0F);
        }
        this.renderHearts(guiGraphics,
                player,
                posX,
                posY,
                heartOffsetByRegen,
                maxHealth,
                currentHealth,
                displayHealth,
                currentAbsorption,
                blink);
        Profiler.get().pop();
    }

    private void renderHearts(GuiGraphicsExtractor guiGraphics, Player player, int posX, int posY, int heartOffsetByRegen, float maxHealth, int currentHealth, int displayHealth, int currentAbsorptionHealth, boolean blink) {
        boolean hardcore = player.level().getLevelData().isHardcore();
        int normalHearts = Math.min(10, Mth.ceil((double) maxHealth / 2.0));
        int maxAbsorptionHearts = 20 - normalHearts;
        int absorptionHearts = Math.min(20 - normalHearts, Mth.ceil((double) currentAbsorptionHealth / 2.0));

        for (int currentHeart = 0; currentHeart < normalHearts + absorptionHearts; ++currentHeart) {

            int currentPosX = posX + (currentHeart % 10) * 8;
            int currentPosY = posY - (currentHeart / 10) * 10;

            if (currentHealth + currentAbsorptionHealth <= 4) {
                currentPosY += this.random.nextInt(2);
            }

            if (currentHeart < normalHearts && heartOffsetByRegen == currentHeart) {
                currentPosY -= 2;
            }

            // renders the black heart outline and background (only visible for half hearts)
            this.blitHeart(Gui.HeartType.CONTAINER, guiGraphics, currentPosX, currentPosY, blink, false, hardcore);
            // then the first call to renderHeart renders the heart from the layer below in case the current layer heart is just half a heart
            // the second call renders the actual heart from the current layer
            if (currentHeart >= normalHearts) {
                int currentAbsorption = currentHeart * 2 - normalHearts * 2;
                if (currentAbsorption < currentAbsorptionHealth) {
                    int maxAbsorptionHealth = maxAbsorptionHearts * 2;
                    boolean halfHeart = currentAbsorption + 1 == currentAbsorptionHealth % maxAbsorptionHealth;
                    boolean layer = currentAbsorptionHealth > maxAbsorptionHealth
                            && currentAbsorption + 1 <= (currentAbsorptionHealth - 1) % maxAbsorptionHealth + 1;
                    if (halfHeart && layer) {
                        Gui.HeartType heartType = forPlayer(player, true, false);
                        this.blitHeart(heartType, guiGraphics, currentPosX, currentPosY, false, false, hardcore);
                    }

                    Gui.HeartType heartType = forPlayer(player, true, layer);
                    this.blitHeart(heartType, guiGraphics, currentPosX, currentPosY, false, halfHeart, hardcore);
                }
            }

            if (blink && currentHeart * 2 < Math.min(20, displayHealth)) {
                boolean halfHeart = currentHeart * 2 + 1 == (displayHealth - 1) % 20 + 1;
                boolean layer = displayHealth > 20 && currentHeart * 2 + 1 <= (displayHealth - 1) % 20 + 1;
                if (halfHeart && layer) {
                    Gui.HeartType heartType = forPlayer(player, false, false);
                    this.blitHeart(heartType, guiGraphics, currentPosX, currentPosY, true, false, hardcore);
                }

                Gui.HeartType heartType = forPlayer(player,
                        false,
                        layer || OverflowingBars.CONFIG.get(ClientConfig.class).health.colorizeFirstRow
                                && currentHeart * 2 + 1 <= (displayHealth - 1) % 20 + 1);
                this.blitHeart(heartType, guiGraphics, currentPosX, currentPosY, true, halfHeart, hardcore);
            }

            if (currentHeart * 2 < Math.min(20, currentHealth)) {
                boolean halfHeart = currentHeart * 2 + 1 == (currentHealth - 1) % 20 + 1;
                boolean layer = currentHealth > 20 && currentHeart * 2 + 1 <= (currentHealth - 1) % 20 + 1;
                if (halfHeart && layer) {
                    Gui.HeartType heartType = forPlayer(player, false, false);
                    this.blitHeart(heartType, guiGraphics, currentPosX, currentPosY, false, false, hardcore);
                }

                Gui.HeartType heartType = forPlayer(player,
                        false,
                        layer || OverflowingBars.CONFIG.get(ClientConfig.class).health.colorizeFirstRow
                                && currentHeart * 2 + 1 <= (currentHealth - 1) % 20 + 1);
                this.blitHeart(heartType, guiGraphics, currentPosX, currentPosY, false, halfHeart, hardcore);
            }
        }
    }

    private void blitHeart(Gui.HeartType heartType, GuiGraphicsExtractor guiGraphics, int posX, int posY, boolean blinking, boolean halfHeart, boolean hardcore) {
        Identifier identifier = heartType.getSprite(hardcore, halfHeart, blinking);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, posX, posY, 9, 9);
    }

    /**
     * @see net.minecraft.client.gui.Gui.HeartType#forPlayer(Player)
     */
    public static Gui.HeartType forPlayer(Player player, boolean absorbing, boolean layer) {
        Gui.HeartType heartType = Gui.HeartType.forPlayer(player);
        if (heartType != Gui.HeartType.NORMAL) {
            return heartType;
        } else {
            boolean inverse = OverflowingBars.CONFIG.get(ClientConfig.class).health.inverseColoring;
            if (layer) {
                return absorbing || !inverse ? ModEnumConstants.LAYER_HEART_TYPE : heartType;
            } else {
                return absorbing ? Gui.HeartType.ABSORBING : (inverse ? ModEnumConstants.LAYER_HEART_TYPE : heartType);
            }
        }
    }
}
