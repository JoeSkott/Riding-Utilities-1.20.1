package net.joeskott.ridingutils.item.custom;

import net.joeskott.ridingutils.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
    double speedEffectMultiplier = 2.0d;
    float flightMotionMultiplier = 1.5f;
    double waterMobBoost = 0.01d;

    // Motion and Damage Handler
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        // Start of interaction
        if(!pLevel.isClientSide()) {

            // If the player is not a passenger we exit this method
            if(!pPlayer.isPassenger()) {
                return super.use(pLevel, pPlayer, pUsedHand);
            }


            // Set variables
            Entity playerMount = pPlayer.getVehicle();
            ItemStack itemSelf = pPlayer.getItemInHand(pUsedHand);
            ItemStack itemOffhand = pPlayer.getOffhandItem();

            boolean offhandIsWhip = false; //TODO change this later
            boolean offhandIsSelf = itemOffhand.is(this);
            boolean isControllable = playerMount instanceof Saddleable;
            boolean cancelMotion = !itemSelf.is(ModItems.LASSO.get()) || offhandIsWhip || offhandIsSelf || isPhysicalVehicle(playerMount) || isControllable;


            // Don't apply effects if on a horse TODO: potentially change later
            if(playerMount instanceof Horse) {
                //playerMount.resetFallDistance();
                return super.use(pLevel, pPlayer, pUsedHand);
            }


            // Add damage random chance
            if(random.nextInt(damageChance) == 0) {
                addItemDamage(pPlayer, itemSelf, damageOnUse);
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

        boolean climbingMob = (playerMount instanceof Spider);

        if(getBlockCollision(playerMount)) {
            addJumpMotion(player, playerMount, climbingMob);
        }

        Vec3 lookAngle = player.getLookAngle();
        Vec3 lastMotion = playerMount.getDeltaMovement();

        boolean offGround = !playerMount.onGround() && lastMotion.y < -0.1f;
        boolean inWater = playerMount.isInWater();
        boolean canFly = (playerMount instanceof FlyingMob);

        // If we're not in the ground or in water (and not a flying or climbing mod)
        // we cancel movement
        if((offGround || inWater) && !canFly && !climbingMob) {
            return;
        }

        // Motion Defined
        Vec3 newMotion = new Vec3(lastMotion.x + (lookAngle.x/2), lastMotion.y, lastMotion.z + (lookAngle.z/2));
        Vec3 newFastMotion = new Vec3(lastMotion.x + (lookAngle.x * speedEffectMultiplier), lastMotion.y, lastMotion.z + (lookAngle.z * speedEffectMultiplier));
        Vec3 newJumpMotion = new Vec3(lookAngle.x/4, lastMotion.y, lookAngle.z/4);
        Vec3 newFlightMotion = new Vec3(lastMotion.x + (lookAngle.x * flightMotionMultiplier), lastMotion.y + (lookAngle.y * flightMotionMultiplier), lastMotion.z + (lookAngle.z * flightMotionMultiplier));

        setLookAngle(playerMount, player);

        if(canFly) {
            playerMount.setDeltaMovement(newFlightMotion);
        } else if (!playerMount.onGround()) {
            playerMount.setDeltaMovement(newJumpMotion);
        } else {
            if(hasSpeedEffect(playerMount)) {
                playerMount.setDeltaMovement(newFastMotion);
            } else {
                playerMount.setDeltaMovement(newMotion);
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

    private boolean hasSpeedEffect(Entity entity) {
        if(entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.hasEffect(MobEffects.MOVEMENT_SPEED);
        }
        return false;
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
        //return blockState.isSolidRender(entity.level(), collidePos);
        //return !blockState.isAir();
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

    private boolean isPhysicalVehicle(Entity entity) {
        if(entity instanceof Boat || entity instanceof Minecart) {
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        // Interaction Start
        if(!pPlayer.level().isClientSide()) {
            if(!pPlayer.isPassenger()) {
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
                }
            }
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    private void addItemDamage(Player player, ItemStack item, int damageOnUse) {
        item.hurtAndBreak(
                damageOnUse,
                player,
                (pPlayer) -> pPlayer.broadcastBreakEvent(pPlayer.getUsedItemHand())
        );
    }


}
