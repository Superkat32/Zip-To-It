package net.superkat.ziptoit.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;

public class ZipToItDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ZipToItItemTagProvider::new);
        pack.addProvider(ZipToItItemModelProvider::new);
        pack.addProvider(ZipToItRecipeProvider::new);
//        pack.addProvider(ZipToItDamageTypeGenerator::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
//        registryBuilder.addRegistry(RegistryKeys.DAMAGE_TYPE, ZipToItDamageTypes::bootstrap);
    }
}
