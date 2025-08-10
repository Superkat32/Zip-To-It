package net.superkat.ziptoit.data;

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
                ZipToItItems.YELLOW_STICKY_HAND
        );
    }
}
