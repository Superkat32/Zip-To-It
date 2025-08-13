package net.superkat.ziptoit.item;

import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.ZipToIt;
import net.superkat.ziptoit.zipcast.color.StickyHandColors;
import net.superkat.ziptoit.zipcast.color.ZipcastColor;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ZipToItItems {

    public static final TagKey<Item> STICKY_HANDS = TagKey.of(RegistryKeys.ITEM, Identifier.of(ZipToIt.MOD_ID, "sticky_hands"));

    public static final ComponentType<StickyHandComponent> STICKY_HAND_COMPONENT_TYPE = registerComponent(
            "zipcaster",
            builder -> builder.codec(StickyHandComponent.CODEC).packetCodec(StickyHandComponent.PACKET_CODEC).cache()
    );

    public static final Item RED_STICKY_HAND = registerStickyHand("red_zippy_hand", StickyHandColors.RED);
    public static final Item ORANGE_STICKY_HAND = registerStickyHand("orange_zippy_hand", StickyHandColors.ORANGE);
    public static final Item YELLOW_STICKY_HAND = registerStickyHand("yellow_zippy_hand", StickyHandColors.YELLOW);
    public static final Item LIME_STICKY_HAND = registerStickyHand("lime_zippy_hand", StickyHandColors.LIME);
    public static final Item GREEN_STICKY_HAND = registerStickyHand("green_zippy_hand", StickyHandColors.GREEN);
    public static final Item BLUE_STICKY_HAND = registerStickyHand("blue_zippy_hand", StickyHandColors.BLUE);
    public static final Item CYAN_STICKY_HAND = registerStickyHand("cyan_zippy_hand", StickyHandColors.CYAN);
    public static final Item LIGHT_BLUE_STICKY_HAND = registerStickyHand("light_blue_zippy_hand", StickyHandColors.LIGHT_BLUE);
    public static final Item PINK_STICKY_HAND = registerStickyHand("pink_zippy_hand", StickyHandColors.PINK);
    public static final Item MAGENTA_STICKY_HAND = registerStickyHand("magenta_zippy_hand", StickyHandColors.MAGENTA);
    public static final Item PURPLE_STICKY_HAND = registerStickyHand("purple_zippy_hand", StickyHandColors.PURPLE);
    public static final Item WHITE_STICKY_HAND = registerStickyHand("white_zippy_hand", StickyHandColors.WHITE);
    public static final Item LIGHT_GRAY_STICKY_HAND = registerStickyHand("light_gray_zippy_hand", StickyHandColors.LIGHT_GRAY);
    public static final Item GRAY_STICKY_HAND = registerStickyHand("gray_zippy_hand", StickyHandColors.GRAY);
    public static final Item BROWN_STICKY_HAND = registerStickyHand("brown_zippy_hand", StickyHandColors.BROWN);
    public static final Item BLACK_STICKY_HAND = registerStickyHand("black_zippy_hand", StickyHandColors.BLACK);

    public static void init() {
        ComponentTooltipAppenderRegistry.addLast(ZipToItItems.STICKY_HAND_COMPONENT_TYPE);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(group -> {
            addStickyHandToItemGroup(group, RED_STICKY_HAND);
            addStickyHandToItemGroup(group, ORANGE_STICKY_HAND);
            addStickyHandToItemGroup(group, YELLOW_STICKY_HAND);
            addStickyHandToItemGroup(group, LIME_STICKY_HAND);
            addStickyHandToItemGroup(group, GREEN_STICKY_HAND);
            addStickyHandToItemGroup(group, BLUE_STICKY_HAND);
            addStickyHandToItemGroup(group, CYAN_STICKY_HAND);
            addStickyHandToItemGroup(group, LIGHT_BLUE_STICKY_HAND);
            addStickyHandToItemGroup(group, PINK_STICKY_HAND);
            addStickyHandToItemGroup(group, MAGENTA_STICKY_HAND);
            addStickyHandToItemGroup(group, PURPLE_STICKY_HAND);
            addStickyHandToItemGroup(group, WHITE_STICKY_HAND);
            addStickyHandToItemGroup(group, LIGHT_GRAY_STICKY_HAND);
            addStickyHandToItemGroup(group, GRAY_STICKY_HAND);
            addStickyHandToItemGroup(group, BROWN_STICKY_HAND);
            addStickyHandToItemGroup(group, BLACK_STICKY_HAND);
        });
    }

    private static void addStickyHandToItemGroup(FabricItemGroupEntries group, Item stickyHand) {
        ItemStack stickyHandStack = new ItemStack(stickyHand);
        stickyHandStack.set(ZipToItItems.STICKY_HAND_COMPONENT_TYPE, stickyHandStack.getDefaultComponents().get(STICKY_HAND_COMPONENT_TYPE));
        group.add(stickyHandStack);
    }

    private static Item registerStickyHand(String id, ZipcastColor color) {
        return registerItem(id,
                StickyHandItem::new,
                new Item.Settings().component(STICKY_HAND_COMPONENT_TYPE, new StickyHandComponent(48, 2.25f, color))
        );
    }

    private static Item registerItem(String id, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ZipToIt.MOD_ID, id));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    private static <T> ComponentType<T> registerComponent(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(ZipToIt.MOD_ID, id), ((ComponentType.Builder)builderOperator.apply(ComponentType.builder())).build());
    }
}
