package net.joeskott.ridingutils.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Random;

public class LassoItem extends Item {
    public LassoItem(Properties pProperties) {
        super(pProperties);
    }

    Random random = new Random();
    int damageChance = 10;
    int damageOnUse = 1;

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        // Start of interaction
        if(!pLevel.isClientSide()) {
            // If the player is not a passenger we exit this method
            if(!pPlayer.isPassenger()) {
                return super.use(pLevel, pPlayer, pUsedHand);
            }

            // Set variables
            ItemStack itemSelf = pPlayer.getItemInHand(pUsedHand);


            if(random.nextInt(damageChance) == 0) {
                addItemDamage(pPlayer, itemSelf, damageOnUse);
                pPlayer.playSound(SoundEvents.LEASH_KNOT_BREAK, 0.2f, 1f);

            }
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private void addItemDamage(Player player, ItemStack item, int damageOnUse) {
        item.hurtAndBreak(
                damageOnUse,
                player,
                (pPlayer) -> pPlayer.broadcastBreakEvent(pPlayer.getUsedItemHand())
        );
    }


}
