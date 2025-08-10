package net.superkat.ziptoit.data;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.Models;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.item.Item;
import net.superkat.ziptoit.item.ZipToItItems;

public class ZipToItItemModelProvider extends FabricModelProvider {
    public ZipToItItemModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        registerStickyHand(itemModelGenerator, ZipToItItems.RED_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.YELLOW_STICKY_HAND);
    }

    private void registerStickyHand(ItemModelGenerator generator, Item stickyHand) {
        ItemModel.Unbaked idle = ItemModels.basic(generator.upload(stickyHand, Models.GENERATED));
        ItemModel.Unbaked using = ItemModels.basic(generator.registerSubModel(stickyHand, "_using", Models.HANDHELD_ROD));
        generator.registerCondition(stickyHand, ItemModels.usingItemProperty(), using, idle);
    }
}
