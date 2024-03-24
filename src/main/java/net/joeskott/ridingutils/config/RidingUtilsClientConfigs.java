package net.joeskott.ridingutils.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RidingUtilsClientConfigs {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec SPEC = null;

    static {
        BUILDER.push("Configs for Riding Utils");
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
