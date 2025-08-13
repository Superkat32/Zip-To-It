package net.superkat.ziptoit.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.superkat.ziptoit.item.ZipToItItems;

import java.util.concurrent.CompletableFuture;

public class ZipToItItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public ZipToItItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture, null);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.valueLookupBuilder(ZipToItItems.STICKY_HANDS).add(
                ZipToItItems.RED_STICKY_HAND,
                ZipToItItems.ORANGE_STICKY_HAND,
                ZipToItItems.YELLOW_STICKY_HAND,
                ZipToItItems.LIME_STICKY_HAND,
                ZipToItItems.GREEN_STICKY_HAND,
                ZipToItItems.BLUE_STICKY_HAND,
                ZipToItItems.CYAN_STICKY_HAND,
                ZipToItItems.LIGHT_BLUE_STICKY_HAND,
                ZipToItItems.PINK_STICKY_HAND,
                ZipToItItems.MAGENTA_STICKY_HAND,
                ZipToItItems.PURPLE_STICKY_HAND,
                ZipToItItems.WHITE_STICKY_HAND,
                ZipToItItems.LIGHT_GRAY_STICKY_HAND,
                ZipToItItems.GRAY_STICKY_HAND,
                ZipToItItems.BROWN_STICKY_HAND,
                ZipToItItems.BLACK_STICKY_HAND
        );
    }
}
