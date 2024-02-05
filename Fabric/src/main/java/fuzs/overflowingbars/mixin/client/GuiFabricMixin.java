package fuzs.overflowingbars.mixin.client;

import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.api.client.SharedGuiHeights;
import fuzs.overflowingbars.client.handler.BarOverlayRenderer;
import fuzs.overflowingbars.client.helper.ChatOffsetHelper;
import fuzs.overflowingbars.config.ClientConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Gui.class)
abstract class GuiFabricMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private int screenWidth;
    @Shadow
    private int screenHeight;
    @Shadow
    protected int displayHealth;

    @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
    private void renderHearts(GuiGraphics guiGraphics, Player player, int x, int y, int height, int i, float f, int j, int k, int l, boolean bl, CallbackInfo callback) {
        if (OverflowingBars.CONFIG.get(ClientConfig.class).health.allowLayers) {
            int leftHeight = 39;
            final int raisedHeight = FabricLoader.getInstance().getObjectShare().get("raised:distance") instanceof Integer distance ? distance : 0;
            BarOverlayRenderer.renderHealthLevelBars(guiGraphics, this.screenWidth, this.screenHeight, this.minecraft, leftHeight + raisedHeight, OverflowingBars.CONFIG.get(ClientConfig.class).health.allowCount);
            BarOverlayRenderer.resetRenderState();
            callback.cancel();
        }
    }

    @ModifyVariable(method = "renderPlayerHealth", at = @At("STORE"), ordinal = 11, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getArmorValue()I"), to = @At(value = "FIELD", target = "Lnet/minecraft/world/effect/MobEffects;REGENERATION:Lnet/minecraft/world/effect/MobEffect;")))
    private int renderPlayerHealth$1(int armorValue, GuiGraphics guiGraphics) {
        Player player = this.getCameraPlayer();
        int leftHeight = 39 + this.overflowingBars$getAdditionalLeftHeight(player);
        final int raisedHeight = FabricLoader.getInstance().getObjectShare().get("raised:distance") instanceof Integer distance ? distance : 0;
        ClientConfig.ArmorRowConfig armorConfig = OverflowingBars.CONFIG.get(ClientConfig.class).armor;
        if (armorConfig.allowLayers || OverflowingBars.CONFIG.get(ClientConfig.class).health.allowLayers) {
            armorValue = 0;
            BarOverlayRenderer.renderArmorLevelBar(guiGraphics, this.screenWidth, this.screenHeight, this.minecraft, leftHeight + raisedHeight, armorConfig.allowCount, !armorConfig.allowLayers);
            BarOverlayRenderer.resetRenderState();
        }
        if (player.getArmorValue() > 0) leftHeight += 10;
        ClientConfig.ToughnessRowConfig toughnessConfig = OverflowingBars.CONFIG.get(ClientConfig.class).toughness;
        if (!toughnessConfig.armorToughnessBar || !toughnessConfig.leftSide) return armorValue;
        BarOverlayRenderer.renderToughnessLevelBar(guiGraphics, this.screenWidth, this.screenHeight, this.minecraft, leftHeight + raisedHeight, toughnessConfig.allowCount, true, !toughnessConfig.allowLayers);
        BarOverlayRenderer.resetRenderState();
        if (Mth.floor(player.getAttributeValue(Attributes.ARMOR_TOUGHNESS)) > 0) leftHeight += 10;
        FabricLoader.getInstance().getObjectShare().put(SharedGuiHeights.OBJECT_SHARE_LEFT_HEIGHT_KEY, new MutableInt(leftHeight));
        return armorValue;
    }

    @Inject(method = "renderPlayerHealth", at = @At("TAIL"))
    private void renderPlayerHealth(GuiGraphics guiGraphics, CallbackInfo callback) {
        ClientConfig.ToughnessRowConfig config = OverflowingBars.CONFIG.get(ClientConfig.class).toughness;
        Player player = this.getCameraPlayer();
        if (player == null) return;
        Optional<MutableInt> leaveMyBarsAloneHeight = FabricLoader.getInstance().getObjectShare().get("leavemybarsalone:rightHeight") instanceof MutableInt height ? Optional.of(height) : Optional.empty();
        int rightHeight = leaveMyBarsAloneHeight.map(MutableInt::intValue).orElseGet(() -> 39 + this.overflowingBars$getAdditionalRightHeight(player));
        if (config.armorToughnessBar && !config.leftSide) {
            final int raisedHeight = FabricLoader.getInstance().getObjectShare().get("raised:distance") instanceof Integer distance ? distance : 0;
            BarOverlayRenderer.renderToughnessLevelBar(guiGraphics, this.screenWidth, this.screenHeight, this.minecraft, rightHeight + config.toughnessBarRowShift * 10 + raisedHeight, config.allowCount, false, !config.allowLayers);
            BarOverlayRenderer.resetRenderState();
            if (Mth.floor(player.getAttributeValue(Attributes.ARMOR_TOUGHNESS)) > 0) {
                int toughnessHeight = 10 + config.toughnessBarRowShift * 10;
                rightHeight += toughnessHeight;
                leaveMyBarsAloneHeight.ifPresent(mutableInt -> mutableInt.add(toughnessHeight));
            }
        }
        FabricLoader.getInstance().getObjectShare().put(SharedGuiHeights.OBJECT_SHARE_RIGHT_HEIGHT_KEY, new MutableInt(rightHeight));
    }

    @Unique
    private int overflowingBars$getAdditionalLeftHeight(Player player) {
        if (OverflowingBars.CONFIG.get(ClientConfig.class).health.allowLayers) {
            return ChatOffsetHelper.twoHealthRows(player) ? 20 : 10;
        } else {
            int i = Mth.ceil(player.getHealth());
            int k = this.displayHealth;
            float f = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), (float) Math.max(k, i));
            int l1 = Mth.ceil(player.getAbsorptionAmount());
            int i2 = Mth.ceil((f + (float) l1) / 2.0F / 10.0F);
            int j2 = Math.max(10 - (i2 - 2), 3);
            return (i2 - 1) * j2 + 10;
        }
    }

    @Unique
    private int overflowingBars$getAdditionalRightHeight(Player player) {
        LivingEntity livingEntity = this.getPlayerVehicleWithHealth();
        int x = this.getVehicleMaxHearts(livingEntity);
        int aa = this.getVisibleVehicleHeartRows(x) - 1;
        int rightHeight = Math.max(1, aa) * 10;
        int y = player.getMaxAirSupply();
        int z = Math.min(player.getAirSupply(), y);
        if (player.isEyeInFluid(FluidTags.WATER) || z < y) {
            rightHeight += 10;
        }
        return rightHeight;
    }

    @Shadow
    private Player getCameraPlayer() {
        throw new IllegalStateException();
    }

    @Shadow
    private LivingEntity getPlayerVehicleWithHealth() {
        throw new IllegalStateException();
    }

    @Shadow
    private int getVehicleMaxHearts(LivingEntity mountEntity) {
        throw new IllegalStateException();
    }

    @Shadow
    private int getVisibleVehicleHeartRows(int mountHealth) {
        throw new IllegalStateException();
    }
}
