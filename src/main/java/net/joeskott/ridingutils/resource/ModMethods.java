package net.joeskott.ridingutils.resource;

import net.joeskott.ridingutils.config.RidingUtilsCommonConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
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

    public static boolean hasSpeedEffect(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.hasEffect(MobEffects.MOVEMENT_SPEED);
        }
        return false;
    }

    public static boolean hasHasteEffect(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.hasEffect(MobEffects.DIG_SPEED);
        }
        return false;
    }

    public static boolean hasLuckEffect(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.hasEffect(MobEffects.LUCK);
        }
        return false;
    }

    public static boolean hasFrenziedEffect(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.hasEffect(MobEffects.DAMAGE_BOOST);
        }
        return false;
    }

    public static int getWhipState (Entity entity) {
        boolean lockSpeedState = RidingUtilsCommonConfigs.disabledSpeedStates.get();
        // If we're only doing one state, keep it at one state at all times
        if(lockSpeedState) {
            return -1;
        }
        boolean speedEffect = ModMethods.hasSpeedEffect(entity);
        boolean hasteEffect = ModMethods.hasHasteEffect(entity);
        boolean luckEffect = ModMethods.hasLuckEffect(entity);

        if (speedEffect && !hasteEffect && !luckEffect) {
            return 0;
        } else if (speedEffect && hasteEffect && !luckEffect) {
            return 1;
        } else if (speedEffect && hasteEffect && luckEffect) {
            return 2;
        }
        return -1;
    }

    public static void displayCantRideActionBarMessage(Entity mount, Player player, ChatFormatting style) {
        String name = I18n.get(mount.getType().toString());
        String text = "Cannot Ride " + name + ". " + name + " is Riled Up!";
        player.displayClientMessage(Component.literal(text).withStyle(style), true);
    }
}
