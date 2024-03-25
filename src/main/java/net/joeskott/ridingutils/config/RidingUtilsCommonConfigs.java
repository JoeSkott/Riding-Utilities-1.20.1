package net.joeskott.ridingutils.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RidingUtilsCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC = null;


    public static ForgeConfigSpec.DoubleValue lassoJumpHeight;
    public static ForgeConfigSpec.DoubleValue lassoWhipFastSpeedBoost;

    public static ForgeConfigSpec.DoubleValue lassoWhipUltraFastSpeedBoost;

    public static ForgeConfigSpec.DoubleValue lassoWhipFrenzySpeedBoost;

    public static ForgeConfigSpec.ConfigValue<Integer> whipEffectDuration;

    public static ForgeConfigSpec.ConfigValue<Integer> whipCompoundEffectDuration;

    public static ForgeConfigSpec.ConfigValue<Integer> whipFastSpeedAmplifier;

    public static ForgeConfigSpec.ConfigValue<Integer> whipUltraFastSpeedAmplifier;

    public static ForgeConfigSpec.ConfigValue<Integer> whipFrenzySpeedAmplifier;

    public static ForgeConfigSpec.ConfigValue<Integer> whipCooldownTicks;

    public static ForgeConfigSpec.ConfigValue<Integer> frenziedCooldownTicks;

    public static ForgeConfigSpec.ConfigValue<Integer> whipWaterCooldownTicks;

    public static ForgeConfigSpec.ConfigValue<Boolean> whipFrenzyErratic;

    public static ForgeConfigSpec.ConfigValue<Boolean> displayState;

    public static ForgeConfigSpec.ConfigValue<Boolean> displayEntityCooldownMessage;


    public static ForgeConfigSpec.ConfigValue<Boolean> whipBuck;

    public static ForgeConfigSpec.ConfigValue<Integer> whipDangerStart;

    public static ForgeConfigSpec.BooleanValue horsesSwimNaturally;

    //public static ForgeConfigSpec.BooleanValue whipCausesHorseContinuousSwimming;

    public static ForgeConfigSpec.ConfigValue<Boolean> disabledSpeedStates;

    static {
        BUILDER.push("Configs for Riding Utilities");

        lassoJumpHeight = BUILDER.comment("How high do mobs jump when using the lasso? (Defaults to 0.5)")
                .defineInRange("Lasso Jump Height", 0.5, 0.1, 2.0);

        lassoWhipFastSpeedBoost = BUILDER.comment("Speed multiplier for when using the lasso with applied whip speed at stage 0 (Defaults to 1.2)")
                .defineInRange("Lasso Whip Fast Speed Boost", 1.2, 0.1, 3.0);

        lassoWhipUltraFastSpeedBoost = BUILDER.comment("Speed multiplier for when using the lasso with applied whip speed at stage 1 (Defaults to 1.5)")
                .defineInRange("Lasso Whip Ultra Fast Speed Boost", 1.5, 0.1, 3.0);

        lassoWhipFrenzySpeedBoost = BUILDER.comment("Speed multiplier for when using the lasso with applied whip speed at stage 2 (Defaults to 2.0)")
                .defineInRange("Lasso Whip Ultra Fast Speed Boost", 2.0, 0.1, 3.0);


        whipEffectDuration = BUILDER.comment("How long does the speed boost last? (Defaults to 160 ticks or 8 seconds)")
                .defineInRange("Whip Speed Duration", 160, 1, 99999999);

        whipCompoundEffectDuration = BUILDER.comment("This is the period after using the whip that repeat usage will increase speed (Defaults to 75 ticks or 3.75 seconds)")
                .defineInRange("Whip Compound Speed Duration", 95, 1, 99999999);

        whipFastSpeedAmplifier = BUILDER.comment("Fast speed amplifier for default controllable mobs (Defaults to 2)")
                .defineInRange("Whip Fast Stage Amplifier", 2, 0, 99999999);

        whipUltraFastSpeedAmplifier = BUILDER.comment("Ultra fast speed amplifier for default controllable mobs (Defaults to 3) ")
                .defineInRange("Whip Ultra Fast Stage Amplifier", 3, 0, 99999999);

        whipFrenzySpeedAmplifier = BUILDER.comment("Frenzy speed amplifier for default controllable mobs (Defaults to 5) ")
                .defineInRange("Whip Frenzy Stage Amplifier", 5, 0, 99999999);

        whipCooldownTicks = BUILDER.comment("How many ticks before the whip can be used again? (Defaults to 80 or 4 seconds)")
                .defineInRange("Whip Cooldown", 80, 1, 99999999);

        whipWaterCooldownTicks = BUILDER.comment("How many ticks for whip cooldown when it's used in water? (Defaults to 70 or 3.5 seconds)")
                .defineInRange("Whip Water Cooldown", 70, 1, 99999999);

        whipBuck = BUILDER.comment("Does the whip have a chance to buck off the rider? (Defaults to true)")
                .define("Whip Buck Chance", true);

        whipDangerStart = BUILDER.comment("When does the risk of side effects begin (at what damage value, higher number = lower durability)? (Defaults to 32)")
                .defineInRange("Whip Buck Danger", 32, 1, 2048);

        whipFrenzyErratic = BUILDER.comment("In frenzy mode, does it cause erratic movement? (Defaults to true)")
                .define("Frenzy Causes Erratic Movement", true);

        frenziedCooldownTicks = BUILDER.comment("How many ticks, after being bucked, before you can ride an entity again? (Defaults to 80 or 4 seconds)")
                .defineInRange("Frenzied Cooldown", 80, 1, 99999999);

        displayEntityCooldownMessage = BUILDER.comment("Display the cooldown message when the entity doesn't want to be ridden (Defaults to true)")
                .define("Display Entity Cooldown Message", true);

        displayState = BUILDER.comment("Display the current speed state? (Defaults to true)")
                .define("Display Speed State", true);

        horsesSwimNaturally = BUILDER.comment("Do horses naturally swim in water (even lava) when they have a rider? (Defaults to true)")
                .define("Horses Swim Naturally", true);

        //whipCausesHorseContinuousSwimming = BUILDER.comment("Do horses move faster and also on their own in water when using the whip? (Defaults to true)")
        //        .define("Horses Swim Automagically", true);

        disabledSpeedStates = BUILDER.comment("If true, locks the speed states to only one state (Defaults to false)")
                .define("Disabled Speed States", false);

        BUILDER.pop();
        SPEC = BUILDER.build();

    }

}
