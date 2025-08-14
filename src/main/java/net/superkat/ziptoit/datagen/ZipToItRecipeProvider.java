package net.superkat.ziptoit.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.TransmuteRecipeJsonBuilder;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;
import net.superkat.ziptoit.item.StickyHandItem;
import net.superkat.ziptoit.item.ZipToItItems;

import java.util.concurrent.CompletableFuture;

public class ZipToItRecipeProvider extends FabricRecipeProvider {
    public ZipToItRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                createInitStickyhand(ZipToItItems.YELLOW_STICKY_HAND, Items.OCHRE_FROGLIGHT);
                createInitStickyhand(ZipToItItems.PURPLE_STICKY_HAND, Items.PEARLESCENT_FROGLIGHT);
                createInitStickyhand(ZipToItItems.LIME_STICKY_HAND, Items.VERDANT_FROGLIGHT);

                generatedDyedStickyHands();
            }

            private void createInitStickyhand(Item stickyHand, Item frogLight) {
                createShapeless(RecipeCategory.TRANSPORTATION, stickyHand)
                        .input(Items.LEAD)
                        .input(Items.SLIME_BALL)
                        .input(frogLight)
                        .group("sticky_hands_from_froglights")
                        .criterion(hasItem(frogLight), conditionsFromItem(frogLight))
                        .offerTo(exporter, getItemPath(stickyHand) + "_from_froglight");
            }

            private void generatedDyedStickyHands() {
                Ingredient stickyHands = this.ingredientFromTag(ZipToItItems.STICKY_HANDS);

                for (DyeColor dyeColor : DyeColor.values()) {
                    DyeItem dyeItem = DyeItem.byColor(dyeColor);
                    Item stickyHandItem = StickyHandItem.getStickyHand(dyeColor);
                    TransmuteRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, stickyHands, Ingredient.ofItem(dyeItem), stickyHandItem)
                            .group("sticky_hand_dye")
                            .criterion(hasItem(dyeItem), this.conditionsFromItem(dyeItem))
                            .offerTo(exporter, "dye_" + getItemPath(stickyHandItem));
                }
            }
        };
    }

    @Override
    public String getName() {
        return "ZipToItRecipeProvider";
    }
}
