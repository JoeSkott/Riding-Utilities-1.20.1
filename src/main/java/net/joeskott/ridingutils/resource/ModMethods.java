package net.joeskott.ridingutils.resource;

import net.minecraft.world.entity.Entity;
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
}
