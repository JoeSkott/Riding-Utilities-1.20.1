package net.joeskott.ridingutils.world;


import net.joeskott.ridingutils.RidingUtils;
import net.joeskott.ridingutils.config.RidingUtilsCommonConfigs;
import net.joeskott.ridingutils.resource.ModMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import static java.lang.Math.round;

@Mod.EventBusSubscriber(modid = RidingUtils.MOD_ID)
public class ModWorldEvents {
    static Random random = new Random();

    private static final int erraticChance = 20;

    @SubscribeEvent
    public static void playerTick(final TickEvent.PlayerTickEvent event) {
        Boolean horsesSwim = RidingUtilsCommonConfigs.horsesSwimNaturally.get();
        Boolean erraticFrenzy = RidingUtilsCommonConfigs.whipFrenzyErratic.get();
        int ejectChance = 250;
        Boolean displayEntityCooldownMessage = RidingUtilsCommonConfigs.displayEntityCooldownMessage.get();
        //Boolean horsesSwimFastWithWhip = RidingUtilsCommonConfigs.whipCausesHorseContinuousSwimming.get();

        if(event.phase == TickEvent.Phase.END && horsesSwim) {
            Player player = event.player;

            if(!player.isPassenger()) {
                return;
            }

            Entity playerMount = player.getVehicle();

            int state = ModMethods.getWhipState(playerMount);

            // Before doing anything, we apply erratic motion is applicable
            if(erraticFrenzy && state > 1) {
                applyErraticFrenzy(playerMount);
            }

            // Eject if Frenzy
            if(!player.level().isClientSide()) {
                if(ModMethods.hasHorseEjectEffect(playerMount)) {
                    playerMount.ejectPassengers();
                }

                if(ModMethods.getWhipState(playerMount) >= 2 && random.nextInt(ejectChance) == 0) {
                    ModMethods.addHorseEjectEffect(playerMount, 1, RidingUtilsCommonConfigs.whipCompoundEffectDuration.get());
                    if(playerMount instanceof Horse) {
                        ((Horse) playerMount).makeMad();
                    }
                }
            }




            // If it's not a horse, it won't work, so we don't proceed with
            // water swim tech
            if(!(playerMount instanceof Horse)){
                return;
            }

            float chance = 0.4f;
            float roll = random.nextFloat(1.0f);

            // If in water and swimming
            if(playerMount.isInWater() && shouldSwim(playerMount, player) && !getLiquidBelow(playerMount, true)) {//getBlockCollision(playerMount, true)) {
                Vec3 currentVelocity = playerMount.getDeltaMovement();

                double upVelocity = 0.3d;
                Vec3 newVelocity = new Vec3(currentVelocity.x, upVelocity, currentVelocity.z);

                playerMount.setDeltaMovement(newVelocity);
            }

            // If there's no liquid under us, return
            if(!getLiquidBelow(playerMount, true)) {
                return;
            }

            if (shouldSwim(playerMount, player)) {
                Vec3 currentVelocity = playerMount.getDeltaMovement();
                Vec3 lookAngle = playerMount.getLookAngle();

                double upVelocity = currentVelocity.y;
                double sine = getSine(player.level().getGameTime(), 1.0D);

                if (currentVelocity.y < 0.0D) {
                    if(sine < 0.9D && roll < chance) { // designed to prevent jumping too much
                        upVelocity = currentVelocity.y + 0.1D + random.nextDouble(0.1D);
                    } else {
                        upVelocity = currentVelocity.y + 0.03D + random.nextDouble(0.1D);
                    }
                }

                Vec3 newVelocity = new Vec3(currentVelocity.x, upVelocity, currentVelocity.z);
                playerMount.setDeltaMovement(newVelocity);
            }
        }
    }

    private static void applyErraticFrenzy(Entity entity) {

        int roll = random.nextInt(erraticChance);
        if(roll != 0) {
            return;
        }

        double movementMultiplier = 0.15D;

        Vec3 currentVelocity = entity.getDeltaMovement();
        Vec3 lookAngle = entity.getLookAngle();
        double addX = (lookAngle.x + random.nextDouble(4.0D) - 2.0D) * movementMultiplier;
        double addY = (lookAngle.y + random.nextDouble(1.0D) - 0.5D) * movementMultiplier;
        double addZ = (lookAngle.z + random.nextDouble(4.0D) - 2.0D) * movementMultiplier;

        // on ground check for y so behavior isn't crazy in the air
        if(!entity.onGround()) {
            addY = 0.0D;
        }

        Vec3 newVelocity = new Vec3(currentVelocity.x + addX, currentVelocity.y + addY, currentVelocity.z + addZ);
        entity.setDeltaMovement(newVelocity);


    }

    private static double getSine(long time, double range) {
        double factor = 0.5D;
        double result = Mth.sin((float) (time * factor)) * range;
        result += range;
        result /= 2.0D;

        return result;
    }

    private static boolean getLiquidBelow(Entity entity, boolean twoSteps) {
        if(entity.onGround()) {
            return false;
        }

        int posX = (int)round(entity.getX());
        int posY = (int)round(entity.getY());
        int posZ = (int)round(entity.getZ());

        BlockPos blockPos = new BlockPos(posX, posY, posZ);

        BlockState blockStateBelow = entity.level().getBlockState(blockPos.below());
        BlockState blockStateBelow2 = entity.level().getBlockState(blockPos.below().below());
        boolean returnValue = blockStateBelow.liquid();

        if(twoSteps){
            returnValue = blockStateBelow.liquid() && blockStateBelow2.liquid();
        }

        return returnValue;
    }

    private static boolean getBlockCollision(Entity entity, boolean belowToo) {
        if(entity.onGround()){
            return false;
        }

        Vec3 lookAngle = entity.getLookAngle();
        Vec3 position = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        double angleX = lookAngle.x;
        double angleZ = lookAngle.z;
        double offsetY = 0.1D;
        boolean returnValue = false;

        int posX = (int)round(lookAngle.x + position.x);
        int posY = (int)round(position.y + offsetY);
        int posZ = (int)round(lookAngle.z + position.z);

        BlockPos collidePos = new BlockPos(posX, posY, posZ);

        BlockState blockState = entity.level().getBlockState(collidePos);

        returnValue = blockState.blocksMotion();

        if(belowToo) {
            BlockState blockStateAbove = entity.level().getBlockState(collidePos.above());
            BlockState blockStateBelow = entity.level().getBlockState(collidePos.below());
            BlockState blockStateBelow2 = entity.level().getBlockState(collidePos.below().below());
            returnValue = blockState.blocksMotion() ||
                    blockStateBelow.blocksMotion() ||
                    blockStateBelow2.blocksMotion() ||
                    blockStateAbove.blocksMotion();
        }

        return returnValue;
    }

    private static boolean shouldSwim(Entity entity, Player player) {
        double boostHeight = 0.0D;
        if(entity instanceof Horse) {
            boostHeight = 0.5D;
        }
        return entity.isInWater() && entity.getFluidHeight(FluidTags.WATER) > entity.getFluidJumpThreshold() + boostHeight;
    }

}
