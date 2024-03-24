package net.joeskott.ridingutils.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RidingUtilsCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC = null;
    public static ForgeConfigSpec.DoubleValue lassoJumpHeight;
    public static ForgeConfigSpec.DoubleValue lassoWhipSpeedBoost;

    public static ForgeConfigSpec.ConfigValue<Integer> whipEffectDuration;

    public static ForgeConfigSpec.ConfigValue<Integer> whipControllableSpeedAmplifier;

    public static ForgeConfigSpec.ConfigValue<Integer> whipCooldownTicks;

    public static ForgeConfigSpec.ConfigValue<Integer> whipWaterCooldownTicks;

    public static ForgeConfigSpec.ConfigValue<Boolean> whipShowsDamage;

    public static ForgeConfigSpec.ConfigValue<Boolean> whipBuck;

    public static ForgeConfigSpec.ConfigValue<Integer> whipDangerStart;

    public static ForgeConfigSpec.BooleanValue horsesSwimNaturally;

    static {
        BUILDER.push("Configs for Riding Utilities");

        lassoJumpHeight = BUILDER.comment("How high do mobs jump when using the lasso? (Defaults to 0.5)")
                .defineInRange("Lasso Jump Height", 0.5, 0.1, 2.0);

        lassoWhipSpeedBoost = BUILDER.comment("Speed multiplier for when using the lasso with applied whip speed (Defaults to 1.2)")
                .defineInRange("Lasso Whip Speed Boost", 1.2, 0.1, 3.0);

        whipEffectDuration = BUILDER.comment("How long does the speed boost last? (Defaults to 140 ticks or 7 seconds)")
                .defineInRange("Whip Speed Duration", 140, 1, 99999999);

        whipControllableSpeedAmplifier = BUILDER.comment("Speed amplifier for default controllable mobs (Defaults to 2)")
                .defineInRange("Whip Speed Amplifier", 2, 0, 99999999);

        whipCooldownTicks = BUILDER.comment("How many ticks before the whip can be used again? (Defaults to 90 or 4 seconds)")
                .defineInRange("Riding Crop Cooldown", 90, 1, 99999999);

        whipWaterCooldownTicks = BUILDER.comment("How many ticks for whip cooldown when it's used in water? (Defaults to 120 or 5 seconds)")
                .defineInRange("Whip Water Cooldown", 120, 1, 99999999);

        whipShowsDamage = BUILDER.comment("Does the whip occasionally cause faux damage even when repaired? (Defaults to true)")
                .define("Whip Fake Damage", true);

        whipBuck = BUILDER.comment("Does the whip have a chance to buck off the rider? (Defaults to true)")
                .define("Whip Buck Chance", true);

        whipDangerStart = BUILDER.comment("When does the risk of side effects begin (at what damage value, higher number = lower durability)? (Defaults to 32)")
                .defineInRange("Whip Buck Danger", 32, 1, 2048);

        horsesSwimNaturally = BUILDER.comment("Do horses naturally swim in water (even lava) when they have a rider? (Defaults to true)")
                .define("Horses Swim Naturally", true);

        BUILDER.pop();
        SPEC = BUILDER.build();

    }

}
