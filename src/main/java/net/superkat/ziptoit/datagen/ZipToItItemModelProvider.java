package net.superkat.ziptoit.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.ModelIds;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.superkat.ziptoit.ZipToIt;
import net.superkat.ziptoit.item.ZipToItItems;

import java.util.Optional;

public class ZipToItItemModelProvider extends FabricModelProvider {

    public static final Model ZIPCAST_USE = new Model(
            Optional.of(Identifier.of(ZipToIt.MOD_ID, "item/using_zippy_sticky_hand")), Optional.empty(), TextureKey.LAYER0
    );

    public ZipToItItemModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        registerStickyHand(itemModelGenerator, ZipToItItems.RED_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.ORANGE_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.YELLOW_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.LIME_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.GREEN_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.BLUE_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.CYAN_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.LIGHT_BLUE_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.PINK_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.MAGENTA_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.PURPLE_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.WHITE_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.LIGHT_GRAY_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.GRAY_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.BROWN_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.BLACK_STICKY_HAND);

        registerStickyHand(itemModelGenerator, ZipToItItems.PRIDE_STICKY_HAND);
        registerStickyHand(itemModelGenerator, ZipToItItems.TRANS_STICKY_HANDER);
    }

    private void registerStickyHand(ItemModelGenerator generator, Item stickyHand) {
        ItemModel.Unbaked idle = ItemModels.basic(generator.upload(stickyHand, Models.GENERATED));
        // cursed
        ItemModel.Unbaked using = ItemModels.basic(ZIPCAST_USE.upload(ModelIds.getItemSubModelId(stickyHand, "_using"), TextureMap.layer0(stickyHand), generator.modelCollector));
        generator.registerCondition(stickyHand, ItemModels.usingItemProperty(), using, idle);

//        generator.register(stickyHand, Models.GENERATED);
    }
}
