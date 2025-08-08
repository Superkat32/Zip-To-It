package net.superkat.ziptoit;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.Models;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.item.Item;

public class ZipToItDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ZipToItItemModelProvider::new);
    }

    public static class ZipToItItemModelProvider extends FabricModelProvider {

        public ZipToItItemModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
//            itemModelGenerator.register(ZipToIt.STICKY_HAND_ITEM, Models.GENERATED);
            Item stickyHand = ZipToIt.STICKY_HAND_ITEM;
            ItemModel.Unbaked idle = ItemModels.basic(itemModelGenerator.upload(stickyHand, Models.GENERATED));
            ItemModel.Unbaked using = ItemModels.basic(itemModelGenerator.registerSubModel(stickyHand, "_using", Models.HANDHELD_ROD));
            itemModelGenerator.registerCondition(stickyHand, ItemModels.usingItemProperty(), using, idle);
        }
    }
}
