package net.rose.satchels.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.TransmuteRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.rose.satchels.common.init.ModItemTags;
import net.rose.satchels.common.init.ModItems;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends FabricRecipeProvider {
    public RecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected net.minecraft.data.recipes.@NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider provider, @NonNull RecipeOutput recipeOutput) {
        return new net.minecraft.data.recipes.RecipeProvider(provider, recipeOutput) {
            @Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);

                shaped(RecipeCategory.TOOLS, ModItems.SATCHEL, 1)
                        .pattern("is")
                        .pattern("ll")
                        .define('i', Items.IRON_INGOT)
                        .define('s', Items.STRING)
                        .define('l', Items.LEATHER)
                        .group("multi_bench")
                        .unlockedBy(getHasName(Items.LEATHER), has(Items.LEATHER))
                        .save(output);

                dyedSatchelRecipe(Items.BLACK_DYE, ModItems.BLACK_SATCHEL);
                dyedSatchelRecipe(Items.BLUE_DYE, ModItems.BLUE_SATCHEL);
                dyedSatchelRecipe(Items.BROWN_DYE, ModItems.BROWN_SATCHEL);
                dyedSatchelRecipe(Items.CYAN_DYE, ModItems.CYAN_SATCHEL);
                dyedSatchelRecipe(Items.GRAY_DYE, ModItems.GRAY_SATCHEL);
                dyedSatchelRecipe(Items.GREEN_DYE, ModItems.GREEN_SATCHEL);
                dyedSatchelRecipe(Items.LIGHT_BLUE_DYE, ModItems.LIGHT_BLUE_SATCHEL);
                dyedSatchelRecipe(Items.LIGHT_GRAY_DYE, ModItems.LIGHT_GRAY_SATCHEL);
                dyedSatchelRecipe(Items.LIME_DYE, ModItems.LIME_SATCHEL);
                dyedSatchelRecipe(Items.MAGENTA_DYE, ModItems.MAGENTA_SATCHEL);
                dyedSatchelRecipe(Items.ORANGE_DYE, ModItems.ORANGE_SATCHEL);
                dyedSatchelRecipe(Items.PINK_DYE, ModItems.PINK_SATCHEL);
                dyedSatchelRecipe(Items.PURPLE_DYE, ModItems.PURPLE_SATCHEL);
                dyedSatchelRecipe(Items.RED_DYE, ModItems.RED_SATCHEL);
                dyedSatchelRecipe(Items.WHITE_DYE, ModItems.WHITE_SATCHEL);
                dyedSatchelRecipe(Items.YELLOW_DYE, ModItems.YELLOW_SATCHEL);

            }

            public void dyedSatchelRecipe(final Item dye, final Item dyedResult) {
                TransmuteRecipeBuilder.transmute(RecipeCategory.TOOLS, this.tag(ModItemTags.SATCHELS), Ingredient.of(dye), dyedResult).group("satchel_dye").unlockedBy(getHasName(dye), this.has(dye)).save(this.output);
            }
        };
    }

    @Override
    public @NonNull String getName() {
        return "RecipeProvider";
    }
}
