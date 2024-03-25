package net.joeskott.ridingutils.sound;

import net.joeskott.ridingutils.RidingUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RidingUtils.MOD_ID);


    // Register sounds
    public static final RegistryObject<SoundEvent> WHIP_CRACKED = registerSoundEvents("whip_cracked");
    public static final RegistryObject<SoundEvent> WHIP_FRENZY = registerSoundEvents("whip_frenzy");




    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(RidingUtils.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
