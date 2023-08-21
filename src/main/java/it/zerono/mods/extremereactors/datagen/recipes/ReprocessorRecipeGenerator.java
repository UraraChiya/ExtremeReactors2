/*
 *
 * ReprocessorRecipeGenerator.java
 *
 * This file is part of Extreme Reactors 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 */

package it.zerono.mods.extremereactors.datagen.recipes;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
import it.zerono.mods.zerocore.lib.recipe.ingredient.FluidStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.ingredient.ItemStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.ItemStackRecipeResult;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ReprocessorRecipeGenerator
        extends AbstractRecipeGenerator {

    public ReprocessorRecipeGenerator(final DataGenerator generatorIn) {
        super(generatorIn);
    }

    //region AbstractRecipeGenerator

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + "Reprocessor recipes";
    }

    /**
     * Registers all recipes to the given consumer.
     */
    @Override
    protected void buildCraftingRecipes(final Consumer<FinishedRecipe> c) {

        // machine recipes

        ReprocessorRecipe.builder(ItemStackRecipeIngredient.from(Content.Items.CYANITE_INGOT.get(), 2),
                    FluidStackRecipeIngredient.from(Fluids.WATER, 1000),
                    ItemStackRecipeResult.from(Content.Items.BLUTONIUM_INGOT.get()))
                .build(c, ExtremeReactors.newID("reprocessor/cyanite_to_blutonium"));

        ReprocessorRecipe.builder(ItemStackRecipeIngredient.from(Content.Items.BLUTONIUM_INGOT.get(), 2),
                        FluidStackRecipeIngredient.from(Content.Fluids.CYANITE_SOURCE.get(), 1000),
                        ItemStackRecipeResult.from(Content.Items.LUDICRITE_INGOT.get()))
                .build(c, ExtremeReactors.newID("reprocessor/blutonium_to_ludicrite"));

        ReprocessorRecipe.builder(ItemStackRecipeIngredient.from(Content.Items.LUDICRITE_INGOT.get(), 2),
                        FluidStackRecipeIngredient.from(Content.Fluids.MAGENTITE_SOURCE.get(), 1000),
                        ItemStackRecipeResult.from(Content.Items.RIDICULITE_INGOT.get()))
                .build(c, ExtremeReactors.newID("reprocessor/ludicrite_to_ridiculite"));

        ReprocessorRecipe.builder(ItemStackRecipeIngredient.from(Content.Items.RIDICULITE_INGOT.get(), 2),
                        FluidStackRecipeIngredient.from(Content.Fluids.ROSSINITE_SOURCE.get(), 1000),
                        ItemStackRecipeResult.from(Content.Items.INANITE_INGOT.get()))
                .build(c, ExtremeReactors.newID("reprocessor/ridiculite_to_inanite"));

        ReprocessorRecipe.builder(ItemStackRecipeIngredient.from(Content.Items.BENITOITE_CRYSTAL.get(), 16),
                        FluidStackRecipeIngredient.from(Content.Fluids.ROSSINITE_SOURCE.get(), 2000),
                        ItemStackRecipeResult.from(Content.Items.INSANITE_INGOT.get()))
                .build(c, ExtremeReactors.newID("reprocessor/rossinite_to_insanite"));

        // reprocessor blocks

        this.casing(c);
        this.glass(c);
        this.controller(c);
        this.port(c, "wasteinjector", Content.Items.REPROCESSOR_WASTEINJECTOR, Items.STICKY_PISTON,
                ContentTags.Items.INGOTS_CYANITE, Tags.Items.DUSTS_REDSTONE);
        this.port(c, "fluidinjector", Content.Items.REPROCESSOR_FLUIDINJECTOR, Items.PISTON,
                Tags.Items.GEMS_PRISMARINE, Tags.Items.GEMS_LAPIS);
        this.port(c, "outputport", Content.Items.REPROCESSOR_OUTPUTPORT, Items.DISPENSER,
                Tags.Items.STORAGE_BLOCKS_LAPIS, Tags.Items.CHESTS);
        this.port(c, "powerport", Content.Items.REPROCESSOR_POWERPORT, Items.REPEATER,
                Tags.Items.STORAGE_BLOCKS_REDSTONE, Tags.Items.GEMS_DIAMOND);
        this.port(c, "collector", Content.Items.REPROCESSOR_COLLECTOR, Items.HOPPER,
                Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_NETHERITE);
    }

    //endregion
    //region internals

    private void casing(final Consumer<FinishedRecipe> c) {
        ShapedRecipeBuilder.shaped(Content.Items.REPROCESSOR_CASING.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('W', Items.WATER_BUCKET)
                .define('C', ContentTags.Items.INGOTS_CYANITE)
                .pattern("ICI")
                .pattern("CWC")
                .pattern("ICI")
                .group(GROUP_GENERAL)
                .unlockedBy("has_item", has(Tags.Items.INGOTS_IRON))
                .save(c, reprocessorRecipeName("casing"));
    }

    private void glass(final Consumer<FinishedRecipe> c) {
        ShapedRecipeBuilder.shaped(Content.Items.REPROCESSOR_GLASS.get())
                .define('C', Content.Items.REPROCESSOR_CASING.get())
                .define('G', Tags.Items.GLASS)
                .pattern("GCG")
                .group(GROUP_GENERAL)
                .unlockedBy("has_item", has(Content.Items.REPROCESSOR_CASING.get()))
                .save(c, reprocessorRecipeName("glass"));
    }

    private void controller(final Consumer<FinishedRecipe> c) {
        ShapedRecipeBuilder.shaped(Content.Items.REPROCESSOR_CONTROLLER.get())
                .define('C', Content.Items.REPROCESSOR_CASING.get())
                .define('Y', ContentTags.Items.INGOTS_CYANITE)
                .define('P', Tags.Items.DUSTS_PRISMARINE)
                .define('E', Tags.Items.GEMS_EMERALD)
                .define('X', Items.COMPARATOR)
                .pattern("CXC")
                .pattern("YEY")
                .pattern("CPC")
                .group(GROUP_GENERAL)
                .unlockedBy("has_item", has(Content.Items.REPROCESSOR_CASING.get()))
                .unlockedBy("has_item2", has(Tags.Items.DUSTS_PRISMARINE))
                .save(c, reprocessorRecipeName("controller"));
    }

    private void port(final Consumer<FinishedRecipe> c, final String name, final Supplier<? extends ItemLike> result,
                      final ItemLike item1, final TagKey<Item> tag2, final TagKey<Item> tag3) {
        ShapedRecipeBuilder.shaped(result.get())
                .define('C', Content.Items.REPROCESSOR_CASING.get())
                .define('1', item1)
                .define('2', tag2)
                .define('3', tag3)
                .pattern("C2C")
                .pattern("313")
                .pattern("C2C")
                .group(GROUP_GENERAL)
                .unlockedBy("has_item", has(Content.Items.REPROCESSOR_CASING.get()))
                .unlockedBy("has_item2", has(item1))
                .save(c, reprocessorRecipeName(name));
    }

    private static ResourceLocation reprocessorRecipeName(final String name) {
        return ExtremeReactors.newID("reprocessor/" + name);
    }

    //endregion
}
