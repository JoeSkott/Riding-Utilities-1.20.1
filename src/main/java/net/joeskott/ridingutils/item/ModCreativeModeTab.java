package net.joeskott.ridingutils.item;

import net.joeskott.ridingutils.RidingUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RidingUtils.MOD_ID);

    public static final RegistryObject<CreativeModeTab> RIDING_UTILS_TAB = CREATIVE_MODE_TABS.register("ridingutils_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.LASSO.get()))
                    .title(Component.translatable("creativetab.ridingutils_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.LASSO.get());
                        pOutput.accept(ModItems.WHIP.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
