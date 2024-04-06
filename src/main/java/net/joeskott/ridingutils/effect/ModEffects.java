package net.joeskott.ridingutils.effect;

import net.joeskott.ridingutils.RidingUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS
            = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, RidingUtils.MOD_ID);

    public static final RegistryObject<MobEffect> WHIP_SPEED = MOB_EFFECTS.register(
            "whip_speed", () -> (new ModMobEffect(MobEffectCategory.NEUTRAL, 16262179))
                    .addAttributeModifier(
                            Attributes.MOVEMENT_SPEED,
                            "b79c6abe-3896-4288-914b-8d3aa0f4b33c",
                            0.2d,
                            AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> COMPOUND_SPEED = MOB_EFFECTS.register(
            "compound_speed", () -> (new ModMobEffect(MobEffectCategory.NEUTRAL, 14270531)));

    public static final RegistryObject<MobEffect> HORSE_EJECT = MOB_EFFECTS.register(
            "horse_eject", () -> (new ModMobEffect(MobEffectCategory.NEUTRAL, 9740385)));


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
