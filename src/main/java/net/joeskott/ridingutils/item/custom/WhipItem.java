package net.joeskott.ridingutils.item.custom;

import net.joeskott.ridingutils.config.RidingUtilsCommonConfigs;
import net.joeskott.ridingutils.effect.ModEffects;
import net.joeskott.ridingutils.item.ModItems;
import net.joeskott.ridingutils.resource.ModMethods;
import net.joeskott.ridingutils.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Horse;
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

    int frenziedCooldownTicks = 80;

    int damageCheck = 32;

    int durationOfEffect = 120;
    int durationOfCompoundEffect = 75;

    boolean doBuckPlayer = true;

    boolean showDamage = false;

    boolean displayState = true;

    int fastAmplifier = 2;
    int ultraFastAmplifier = 3;
    int frenzyAmplifier = 7;

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

            // Cancel if is player
            if(playerMount instanceof Player) {
                return super.use(pLevel, pPlayer, pUsedHand);
            }

            int maxDamage = itemSelf.getMaxDamage();
            int currentDamage = itemSelf.getDamageValue();
            int chanceRange = (maxDamage - currentDamage + 1)/2;

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
                if(ModMethods.getWhipState(playerMount) > 0) {
                    activateFrenzySound(playerMount);
                } else {
                    activateWhipSound(playerMount);
                }


                // Handle cooldowns
                if(onGround) {
                    pPlayer.getCooldowns().addCooldown(this, cooldownTicks);
                } else {
                    pPlayer.getCooldowns().addCooldown(this, waterCooldownTicks);
                }

                // Handle damage
                ModMethods.addItemDamage(pPlayer, itemSelf, damageOnUse);
                rollForHPDamage(pPlayer, playerMount, chanceRange, currentDamage, maxDamage);
                //addSpeed(playerMount, effectAmplifier, durationOfEffect);
                addVariableEffect(playerMount, pPlayer, durationOfEffect);
            }

            // Handle buck
            if(ejectPlayer && doBuckPlayer) {
                // Called if bad stuff happened oops
                buckPlayer(pPlayer, playerMount);
                ejectPlayer = false;
            } else if (ejectPlayer) {
                ejectPlayer = false;
            }

        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private void rollForHPDamage(Player pPlayer, Entity entity, int chanceRange, int currentDamage, int maxDamage) {
        int roll = random.nextInt(chanceRange);
        if(currentDamage >= damageCheck && roll == 0) {
            doRealDamageAndSideEffects(pPlayer, entity);
        }
    }

    private void doRealDamageAndSideEffects(Player pPlayer, Entity entity) {
        ejectPlayer = random.nextBoolean();
        float hurtAmount = random.nextFloat(2.0f);
        doHurt(entity, pPlayer, hurtAmount);
    }

    private void doHurt(Entity entity, Player player, float hurtAmount) {
        if(!entity.onGround()) {
            return;
        }

        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            boolean isHorse = entity instanceof Horse;

            /*if(showDamage){
                Boolean chance = random.nextInt(3) == 0;
                if(chance) {
                    livingEntity.hurt(player.damageSources().generic(), hurtAmount);
                }
            }*/

            livingEntity.hurt(player.damageSources().generic(), hurtAmount);

            if (isHorse) {
                int bound = 3;
                if(!showDamage) {
                    bound = 2;
                }

                int choose = random.nextInt(bound);
                float pitch = getVariablePitch(0.3f);

                switch (choose) {
                    case 0 -> entity.playSound(SoundEvents.HORSE_ANGRY, 1.0f, pitch);
                    case 1 -> entity.playSound(SoundEvents.HORSE_BREATHE, 1.0f, pitch);
                    case 2 -> entity.playSound(SoundEvents.HORSE_HURT, 1.0f, pitch);
                }
            }
        }
    }

    private void buckPlayer(Player player, Entity playerMount) {
        playerMount.ejectPassengers();
        if(player.isPassenger()) {
            return;
        }
        player.stopFallFlying();
    }

    private void activateWhipSound(Entity entity) {
        entity.level().playSeededSound(null, entity.getX(), entity.getY(), entity.getZ(),
                ModSounds.WHIP_CRACKED.get(), SoundSource.BLOCKS, 1f, getVariablePitch(0.4f) - 0.4f, 0);
    }

    private void activateFrenzySound(Entity entity) {
        entity.level().playSeededSound(null, entity.getX(), entity.getY(), entity.getZ(),
                ModSounds.WHIP_FRENZY.get(), SoundSource.BLOCKS, 1.7f, 1f, 0);
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

    private void addVariableEffect(Entity entity, Player player, int duration) {
        int state = ModMethods.getWhipState(entity);
        removeEffects(entity);

        switch (state) {
            case -1:
                addWhipSpeedEffect(entity, fastAmplifier, duration);
                if(displayState) {
                    player.displayClientMessage(Component.literal("Fast").withStyle(ChatFormatting.GREEN), true);
                }
                break;
            case 0:
                addWhipSpeedEffect(entity, ultraFastAmplifier, duration);
                addCompoundSpeedEffect(entity, 1, durationOfCompoundEffect);
                doBuckChance(entity,  player, 80, 40, false);
                if(displayState) {
                    player.displayClientMessage(Component.literal("Ultra Fast").withStyle(ChatFormatting.YELLOW), true);
                }

                break;
            case 1:
                addWhipSpeedEffect(entity, frenzyAmplifier, duration);
                addCompoundSpeedEffect(entity, 1, durationOfCompoundEffect);
                doBuckChance(entity,  player, 10, 5, false);
                if(displayState) {
                    player.displayClientMessage(Component.literal("Frenzy").withStyle(ChatFormatting.DARK_RED), true);
                }

                break;
            case 2:
                addWhipSpeedEffect(entity, frenzyAmplifier, duration);
                addCompoundSpeedEffect(entity, 1, duration);
                // Add particle effects only if it's not a horse
                if (!(entity instanceof Horse)) {
                    ModMethods.addHorseEjectEffect(entity, 1, duration);
                }
                doBuckChance(entity, player, 3, 4, true);
                if(displayState) {
                    player.displayClientMessage(Component.literal("Frenzy").withStyle(ChatFormatting.RED), true);
                }
                break;
        }

    }

    private void doBuckChance(Entity entity, Player player, int bound, int fauxBound, boolean fauxDamage) {
        int randInt = random.nextInt(bound);
        int randInt2 = 0;
        if (randInt == 0) {
            buckPlayer(player, entity);
            if(entity instanceof Horse) {
                ((Horse) entity).makeMad();
            }
            addCompoundSpeedEffect(entity, 1, frenziedCooldownTicks);
            ModMethods.addHorseEjectEffect(entity, 1, frenziedCooldownTicks);
        } else if(fauxDamage) {
            randInt2 = random.nextInt(fauxBound);
            if(randInt2 == 0) {
                doHurt(entity, player, 0.0f);
            } else if(randInt2 == 1 && entity instanceof Horse) {
                ((Horse) entity).makeMad();
            }
        }
    }


    private void removeEffects(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            livingEntity.removeAllEffects();
        }
    }

    private void addWhipSpeedEffect(Entity entity, int amplifier, int duration) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            MobEffectInstance whipSpeedEffect = new MobEffectInstance(
                    ModEffects.WHIP_SPEED.get(),
                    duration,
                    amplifier,
                    false,
                    false,
                    false);
            livingEntity.addEffect(whipSpeedEffect);
        }
    }

    private void addCompoundSpeedEffect(Entity entity, int amplifier, int duration) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            MobEffectInstance compoundSpeedEffect = new MobEffectInstance(
                    ModEffects.COMPOUND_SPEED.get(),
                    duration,
                    amplifier,
                    false,
                    false,
                    false);
            livingEntity.addEffect(compoundSpeedEffect);
        }
    }


    private float getVariablePitch(float maxVariance) {
        float pitchAdjust = random.nextFloat(maxVariance) - random.nextFloat(maxVariance);
        return 1.2f + pitchAdjust;
    }

    private void updateValuesFromConfig() {
        cooldownTicks = RidingUtilsCommonConfigs.whipCooldownTicks.get();
        frenziedCooldownTicks = RidingUtilsCommonConfigs.frenziedCooldownTicks.get();
        waterCooldownTicks = RidingUtilsCommonConfigs.whipWaterCooldownTicks.get();
        damageCheck = RidingUtilsCommonConfigs.whipDangerStart.get();
        durationOfEffect = RidingUtilsCommonConfigs.whipEffectDuration.get();
        durationOfCompoundEffect = RidingUtilsCommonConfigs.whipCompoundEffectDuration.get();
        doBuckPlayer = RidingUtilsCommonConfigs.whipBuck.get();
        fastAmplifier = RidingUtilsCommonConfigs.whipFastSpeedAmplifier.get();
        ultraFastAmplifier = RidingUtilsCommonConfigs.whipUltraFastSpeedAmplifier.get();
        frenzyAmplifier = RidingUtilsCommonConfigs.whipFrenzySpeedAmplifier.get();
        displayState = RidingUtilsCommonConfigs.displayState.get();
    }
}
