package net.joeskott.ridingutils.item.custom;

import net.joeskott.ridingutils.config.RidingUtilsClientConfigs;
import net.joeskott.ridingutils.config.RidingUtilsCommonConfigs;
import net.joeskott.ridingutils.item.ModItems;
import net.joeskott.ridingutils.resource.ModMethods;
import net.joeskott.ridingutils.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class WhipItem extends Item {
    public WhipItem(Properties pProperties) {
        super(pProperties);
    }

    Random random = new Random();

    boolean ejectPlayer = false;

    int damageOnUse = 1;

    int cooldownTicks = 20;

    int waterCooldownTicks = 100;

    int damageCheck = 32;

    int durationOfEffect = 120;

    boolean doBuckPlayer = true;

    boolean showDamage = false;

    int effectAmplifier = 2;

    double motionBoost = 0.4d;
    double waterMotionBoost = 0.4d;

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        // Start of interaction
        if(!pLevel.isClientSide()) {

            // Exit code if not passenger. TODO: possibly change this
            if(!pPlayer.isPassenger()) {
                return super.use(pLevel, pPlayer, pUsedHand);
            }
            // Cancel if is physical vehicle
            if(ModMethods.isPhysicalVehicle(pPlayer.getVehicle())) {
                return super.use(pLevel, pPlayer, pUsedHand);
            }

            updateValuesFromConfig();

            Entity playerMount = pPlayer.getVehicle();

            ItemStack itemSelf = pPlayer.getItemInHand(pUsedHand);

            ItemStack itemOffhand = pPlayer.getOffhandItem();

            boolean offhandIsLasso = itemOffhand.is(ModItems.LASSO.get());

            int maxDamage = itemSelf.getMaxDamage(); //TODO
            int currentDamage = itemSelf.getDamageValue(); //TODO

            boolean onGround = playerMount.onGround();
            boolean inWater = playerMount.isInWater();

            // Add motion :
            if(onGround) {
                addMotion(playerMount);
            } else if (inWater) {
                addWaterMotion(playerMount);
            }



            // Do activation
            if(onGround || inWater){
                activateWhipSound(playerMount);

                // Handle cooldowns
                if(onGround) {
                    pPlayer.getCooldowns().addCooldown(this, cooldownTicks);
                } else {
                    pPlayer.getCooldowns().addCooldown(this, waterCooldownTicks);
                }

                // Handle damage
                ModMethods.addItemDamage(pPlayer, itemSelf, damageOnUse);
                //rollForHPDamage
                addSpeed(playerMount, effectAmplifier, durationOfEffect);
            }

            // Handle buck
            if(ejectPlayer && doBuckPlayer) {
                // Called if bad stuff happened oops
                playerMount.ejectPassengers();
                buckPlayer(pPlayer, playerMount);
                ejectPlayer = false;
            } else if (ejectPlayer) {
                ejectPlayer = false;
            }

        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private void buckPlayer(Player player, Entity playerMount) {
        if(player.isPassenger()) {
            return;
        }
        player.stopFallFlying();
    }

    private void activateWhipSound(Entity entity) {
        entity.level().playSeededSound(null, entity.getX(), entity.getY(), entity.getZ(),
                ModSounds.WHIP_CRACKED.get(), SoundSource.BLOCKS, 1f, getVariablePitch(0.4f) - 0.4f, 0);
    }

    private void addMotion(Entity entity) {
        Vec3 lookAngle = entity.getLookAngle();
        Vec3 lastMotion = entity.getDeltaMovement();
        Vec3 newMotion = new Vec3(
                lastMotion.x + lookAngle.x,
                lastMotion.y + lookAngle.y + motionBoost,
                lastMotion.z + lookAngle.z);

        entity.setDeltaMovement(newMotion);
    }

    private void addWaterMotion(Entity entity) {
        Vec3 lookAngle = entity.getLookAngle();
        Vec3 newMotion = new Vec3(lookAngle.x, waterMotionBoost, lookAngle.z);
        entity.setDeltaMovement(newMotion);
    }

    private void addSpeed(Entity entity, int amplifier, int duration) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            MobEffectInstance speedEffect = new MobEffectInstance(
                    MobEffects.MOVEMENT_SPEED,
                    duration,
                    amplifier,
                    false,
                    false,
                    false);
            livingEntity.addEffect(speedEffect);
        }

    }

    private float getVariablePitch(float maxVariance) {
        float pitchAdjust = random.nextFloat(maxVariance) - random.nextFloat(maxVariance);
        return 1.2f + pitchAdjust;
    }

    private void updateValuesFromConfig() {
        cooldownTicks = RidingUtilsCommonConfigs.whipCooldownTicks.get();
        waterCooldownTicks = RidingUtilsCommonConfigs.whipWaterCooldownTicks.get();
        damageCheck = RidingUtilsCommonConfigs.whipDangerStart.get();
        durationOfEffect = RidingUtilsCommonConfigs.whipEffectDuration.get();
        doBuckPlayer = RidingUtilsCommonConfigs.whipBuck.get();
        showDamage = RidingUtilsCommonConfigs.whipShowsDamage.get();
        effectAmplifier = RidingUtilsCommonConfigs.whipControllableSpeedAmplifier.get();
    }
}
