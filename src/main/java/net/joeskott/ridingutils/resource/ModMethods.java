package net.joeskott.ridingutils.resource;

import net.joeskott.ridingutils.config.RidingUtilsCommonConfigs;
import net.joeskott.ridingutils.effect.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;

public class ModMethods {
    public static boolean isPhysicalVehicle(Entity entity) {
        if(entity instanceof Boat || entity instanceof Minecart) {
            return true;
        }
        return false;
    }

    public static void addItemDamage(Player player, ItemStack item, int damageOnUse) {
        item.hurtAndBreak(
                damageOnUse,
                player,
                (pPlayer) -> pPlayer.broadcastBreakEvent(pPlayer.getUsedItemHand())
        );
    }

//    public static boolean hasFrenziedEffect(Entity entity) {
//        if(entity instanceof LivingEntity) {
//            LivingEntity livingEntity = (LivingEntity) entity;
//            return livingEntity.hasEffect(MobEffects.DAMAGE_BOOST);
//        }
//        return false;
//    }

    public static boolean hasCompoundSpeedEffect(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.hasEffect(ModEffects.COMPOUND_SPEED.get());
        }
        return false;
    }

    public static boolean hasHorseEjectEffect(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.hasEffect(ModEffects.HORSE_EJECT.get());
        }
        return false;
    }

    public static int hasWhipSpeedEffectLevel(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if (!livingEntity.hasEffect(ModEffects.WHIP_SPEED.get())) {
                return -1;
            }
            return livingEntity.getEffect(ModEffects.WHIP_SPEED.get()).getAmplifier();
        }
        return -1;
    }



    public static int getWhipState (Entity entity) {
        boolean lockSpeedState = RidingUtilsCommonConfigs.disabledSpeedStates.get();
        // If we're only doing one state, keep it at one state at all times
        if(lockSpeedState) {
            return -1;
        }

        int effectLevel = hasWhipSpeedEffectLevel(entity);

        boolean levelNone = effectLevel <= -1;
        boolean level0 = effectLevel <= RidingUtilsCommonConfigs.whipFastSpeedAmplifier.get();
        boolean level1 = effectLevel <= RidingUtilsCommonConfigs.whipUltraFastSpeedAmplifier.get();
        boolean level2 = effectLevel > RidingUtilsCommonConfigs.whipUltraFastSpeedAmplifier.get();

        boolean compoundedSpeed = hasCompoundSpeedEffect(entity);

        if (levelNone) {
            return -1;
        }

        if (level0 && compoundedSpeed) {
            return 0;
        }

        if (level1) {
            if(!compoundedSpeed) {
                return 0;
            }
            return 1;
        }

        if (level2) {
            if(!compoundedSpeed) {
                return 1;
            }
            return 2;
        }

        return -1;
    }

    public static void addHorseEjectEffect(Entity entity, int amplifier, int duration) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            MobEffectInstance horseEjectEffect = new MobEffectInstance(
                    ModEffects.COMPOUND_SPEED.get(),
                    duration,
                    amplifier,
                    false,
                    RidingUtilsCommonConfigs.enableRiledUpParticles.get(),
                    false);
            livingEntity.addEffect(horseEjectEffect);
        }
    }

    public static void displayCantRideActionBarMessage(Entity mount, Player player, ChatFormatting style) {
        String name = I18n.get(mount.getType().toString());
        String text = "Cannot Ride " + name + ". " + name + " is Riled Up!";
        player.displayClientMessage(Component.literal(text).withStyle(style), true);
    }
}
