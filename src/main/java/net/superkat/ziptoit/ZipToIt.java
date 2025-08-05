package net.superkat.ziptoit;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.item.StickyHandComponent;
import net.superkat.ziptoit.item.StickyHandItem;
import net.superkat.ziptoit.network.ZipToItPackets;
import net.superkat.ziptoit.network.ZipToItServerNetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ZipToIt implements ModInitializer {
	public static final String MOD_ID = "ziptoit";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ComponentType<StickyHandComponent> STICKY_HAND_COMPONENT_TYPE = registerComponent(
			"zip_range",
			builder -> builder.codec(StickyHandComponent.CODEC).packetCodec(StickyHandComponent.PACKET_CODEC).cache()
	);

	public static final Item STICKY_HAND_ITEM = registerItem(
			"sticky_hand",
			StickyHandItem::new,
			new Item.Settings().component(STICKY_HAND_COMPONENT_TYPE, new StickyHandComponent(48))
	);

	@Override
	public void onInitialize() {
		ComponentTooltipAppenderRegistry.addLast(STICKY_HAND_COMPONENT_TYPE);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(group -> {
			ItemStack stickyHandStack = new ItemStack(STICKY_HAND_ITEM);
			stickyHandStack.set(STICKY_HAND_COMPONENT_TYPE, new StickyHandComponent(48));
			group.add(stickyHandStack);
		});

		ZipToItPackets.init();
		ZipToItServerNetworkHandler.init();
	}

	private static Item registerItem(String id, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, id));
		Item item = itemFactory.apply(settings.registryKey(itemKey));
		Registry.register(Registries.ITEM, itemKey, item);
		return item;
	}

	private static <T> ComponentType<T> registerComponent(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(MOD_ID, id), ((ComponentType.Builder)builderOperator.apply(ComponentType.builder())).build());
	}
}