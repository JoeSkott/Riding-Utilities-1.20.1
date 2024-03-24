package net.joeskott.ridingutils.item;

import net.joeskott.ridingutils.RidingUtils;
import net.joeskott.ridingutils.item.custom.LassoItem;
import net.joeskott.ridingutils.item.custom.MetalDetectorItem;
import net.joeskott.ridingutils.item.custom.WhipItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RidingUtils.MOD_ID);

    public static final RegistryObject<Item> TEST_ITEM = ITEMS.register("testitem",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TEST_ITEM_2 = ITEMS.register("testitem2",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> METAL_DETECTOR = ITEMS.register("metal_detector",
            () -> new MetalDetectorItem(new Item.Properties().durability(100)));


    public static final RegistryObject<Item> LASSO = ITEMS.register("lasso",
            () -> new LassoItem(new Item.Properties().durability(64)));

    public static final RegistryObject<Item> WHIP = ITEMS.register("whip",
            () -> new WhipItem(new Item.Properties().durability(256)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
