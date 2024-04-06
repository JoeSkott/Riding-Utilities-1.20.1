package net.joeskott.ridingutils;

import com.mojang.logging.LogUtils;
import net.joeskott.ridingutils.config.RidingUtilsClientConfigs;
import net.joeskott.ridingutils.config.RidingUtilsCommonConfigs;
import net.joeskott.ridingutils.effect.ModEffects;
import net.joeskott.ridingutils.item.ModCreativeModeTab;
import net.joeskott.ridingutils.item.ModItems;
import net.joeskott.ridingutils.sound.ModSounds;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RidingUtils.MOD_ID)
public class RidingUtils
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "ridingutils";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public RidingUtils() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Config
        //ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, RidingUtilsClientConfigs.SPEC, "ridingutilities-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RidingUtilsCommonConfigs.SPEC, "ridingutilities-common.toml");

        // Creative Tab
        ModCreativeModeTab.register(modEventBus);

        // Register Items
        ModItems.register(modEventBus);

        // Register Sounds
        ModSounds.register(modEventBus);

        // Register Effects
        ModEffects.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
