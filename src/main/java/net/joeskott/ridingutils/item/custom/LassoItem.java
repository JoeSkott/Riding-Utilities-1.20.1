package net.joeskott.ridingutils.item.custom;

import net.joeskott.ridingutils.config.RidingUtilsCommonConfigs;
import net.joeskott.ridingutils.item.ModItems;
import net.joeskott.ridingutils.resource.ModMethods;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

import static java.lang.Math.round;

public class LassoItem extends Item {
    public LassoItem(Properties pProperties) {
        super(pProperties);
    }

    Random random = new Random();

    int damageChance = 10;
    int damageOnUse = 1;

    double jumpHeight = 0.5d;
    double fastSpeedEffectMultiplier = 1.2d;
    double ultraSpeedEffectMultiplier = 1.4d;
    double frenzyEffectMultiplier = 1.8d;
    float flightMotionMultiplier = 1.3f;
    double waterMobBoost = 0.01d;

    boolean displayEntityCooldownMessage = true;

    // Motion and Damage Handler
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        // Start of interaction
        if(!pLevel.isClientSide()) {

            // If the player is not a passenger we exit this method
            if(!pPlayer.isPassenger()) {
                return super.use(pLevel, pPlayer, pUsedHand);
            }

            updateValuesFromConfig();

            // Set variables
            Entity playerMount = pPlayer.getVehicle();
            ItemStack itemSelf = pPlayer.getItemInHand(pUsedHand);
            ItemStack itemOffhand = pPlayer.getOffhandItem();

            boolean offhandIsWhip = itemOffhand.is(ModItems.WHIP.get());
            boolean offhandIsSelf = itemOffhand.is(this);
            boolean isControllable = playerMount instanceof Saddleable;
            boolean cancelMotion = !itemSelf.is(ModItems.LASSO.get()) || offhandIsWhip || ModMethods.isPhysicalVehicle(playerMount) || isControllable;
            //boolean cancelMotion = !itemSelf.is(ModItems.LASSO.get()) || offhandIsWhip || offhandIsSelf || ModMethods.isPhysicalVehicle(playerMount) || isControllable;


            // Don't apply effects if on a horse TODO: potentially change later
            if(playerMount instanceof Horse) {
                //playerMount.resetFallDistance();
                return super.use(pLevel, pPlayer, pUsedHand);
            }


            // Add damage random chance
            if(random.nextInt(damageChance) == 0) {
                ModMethods.addItemDamage(pPlayer, itemSelf, damageOnUse);
                pPlayer.playSound(SoundEvents.LEASH_KNOT_BREAK, 0.2f, 1f);
            }

            // Add motion
            if(!cancelMotion) {
                // If we're in water add water motion, otherwise regular motion
                if(playerMount.isInWater()) {
                    addWaterMotion(pPlayer, playerMount);
                } else {
                    addMotion(pPlayer, playerMount);
                }
            }

        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private void addMotion(Player player, Entity playerMount) {

        //boolean climbingMob = (playerMount instanceof Spider);

        // Replacement for Jumping
        if (playerMount.getStepHeight() < 1.0f) {
            playerMount.setMaxUpStep(1.0f);
        }

        Vec3 lookAngle = player.getLookAngle();
        Vec3 lastMotion = playerMount.getDeltaMovement();

        boolean offGround = !playerMount.onGround() && lastMotion.y < -0.1f;
        boolean inWater = playerMount.isInWater();
        boolean canFly = (playerMount instanceof FlyingMob);

        // If we're not in the ground or in water (and not a flying mob)
        // we cancel movement
        if((offGround || inWater) && !canFly) {
            return;
        }

        // Motion Defined
        Vec3 newMotion = new Vec3(lastMotion.x + (lookAngle.x/2), lastMotion.y, lastMotion.z + (lookAngle.z/2));
        Vec3 newFastMotion = new Vec3(lastMotion.x + (lookAngle.x * fastSpeedEffectMultiplier), lastMotion.y, lastMotion.z + (lookAngle.z * fastSpeedEffectMultiplier));
        Vec3 newUltraFastMotion = new Vec3(lastMotion.x + (lookAngle.x * ultraSpeedEffectMultiplier), lastMotion.y, lastMotion.z + (lookAngle.z * ultraSpeedEffectMultiplier));
        Vec3 newFrenzyMotion = new Vec3(lastMotion.x + (lookAngle.x * frenzyEffectMultiplier), lastMotion.y, lastMotion.z + (lookAngle.z * frenzyEffectMultiplier));

        Vec3 newJumpMotion = new Vec3(lookAngle.x/4, lastMotion.y, lookAngle.z/4);
        Vec3 newFlightMotion = new Vec3(lastMotion.x + (lookAngle.x * flightMotionMultiplier), lastMotion.y + (lookAngle.y * flightMotionMultiplier), lastMotion.z + (lookAngle.z * flightMotionMultiplier));

        setLookAngle(playerMount, player);

        if(canFly) {
            playerMount.setDeltaMovement(newFlightMotion);
        } else if (!playerMount.onGround()) {
            playerMount.setDeltaMovement(newJumpMotion);
        } else {
            // Determines our current speed
            int state = ModMethods.getWhipState(playerMount);

            switch (state){
                case 0 -> playerMount.setDeltaMovement(newFastMotion);
                case 1 -> playerMount.setDeltaMovement(newUltraFastMotion);
                case 2 -> playerMount.setDeltaMovement(newFrenzyMotion);
                default -> playerMount.setDeltaMovement(newMotion);
            }
        }
    }

    private void addWaterMotion(Player player, Entity playerMount) {
        boolean offGround = (!playerMount.onGround() && !playerMount.isInWater());
        boolean canFly = (playerMount instanceof FlyingMob);
        boolean waterMob = (playerMount instanceof WaterAnimal);

        // If we're not in the water and we can't fly, cancel
        if(offGround && !canFly) {
            return;
        }

        Vec3 lookAngle = player.getLookAngle();
        Vec3 lastMotion = playerMount.getDeltaMovement();

        double boost = (waterMob) ? waterMobBoost : lastMotion.y;

        Vec3 newMotion = new Vec3(lastMotion.x + (lookAngle.x/4), boost, lastMotion.z + (lookAngle.z/4));

        playerMount.setDeltaMovement(newMotion);
        setLookAngle(playerMount, player);
    }

    private void addJumpMotion(Player player, Entity playerMount, boolean climbingMob) {
        boolean isOnGround = playerMount.onGround();
        if ((!isOnGround && !climbingMob) || getBlockCeilingCollision(player)) {
            return;
        }

        playerMount.resetFallDistance();

        Vec3 lastMotion = playerMount.getDeltaMovement();
        setLookAngle(playerMount, player);

        Vec3 newMotion = new Vec3(lastMotion.x, jumpHeight, lastMotion.z);
        playerMount.setDeltaMovement(newMotion);
    }

    private void setLookAngle(Entity to_entity, Entity from_entity) {
        float yRot = from_entity.getYRot();
        to_entity.setYRot(yRot);
    }

    private boolean getBlockCeilingCollision(Entity entity) {
        BlockPos collidePos = entity.blockPosition().above();
        BlockState blockState = entity.level().getBlockState(collidePos);
        return blockState.isSolid();
    }

    private boolean getBlockCollision(Entity entity) {
        Vec3 lookAngle = entity.getLookAngle();
        double offsetY = 0.1f;

        int posX = (int)round(lookAngle.x + entity.getX());
        int posZ = (int)round(lookAngle.z + entity.getZ());
        int posY = (int)round(entity.getY() + offsetY);

        BlockPos collidePos = new BlockPos(posX, posY, posZ);
        BlockState blockState = entity.level().getBlockState(collidePos);

        return blockState.isSolid();

        //return blockState.isSolidRender(entity.level(), collidePos);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        // Interaction Start
        if(!pPlayer.level().isClientSide()) {
            if(!pPlayer.isPassenger()) {

                if(ModMethods.getWhipState(pInteractionTarget) >= 2) {
                    if(displayEntityCooldownMessage) {
                        ModMethods.displayCantRideActionBarMessage(pInteractionTarget, pPlayer, ChatFormatting.GOLD);
                    }

                    return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
                }

                boolean isAdult = true;
                boolean ageable = pInteractionTarget instanceof AgeableMob;

                if(ageable) {
                    if(((AgeableMob) pInteractionTarget).getAge() < 0) {
                        isAdult = false;
                    }
                }

                if(isAdult) {
                    pPlayer.startRiding(pInteractionTarget);
                    pInteractionTarget.playSound(SoundEvents.PIG_SADDLE, 1.0f, 1.0f);
                    updateValuesFromConfig();
                }
            }
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    private void updateValuesFromConfig() {
        jumpHeight = RidingUtilsCommonConfigs.lassoJumpHeight.get();
        fastSpeedEffectMultiplier = RidingUtilsCommonConfigs.lassoWhipFastSpeedBoost.get();
        ultraSpeedEffectMultiplier = RidingUtilsCommonConfigs.lassoWhipUltraFastSpeedBoost.get();
        frenzyEffectMultiplier = RidingUtilsCommonConfigs.lassoWhipFrenzySpeedBoost.get();
        displayEntityCooldownMessage = RidingUtilsCommonConfigs.displayEntityCooldownMessage.get();
    }
}
