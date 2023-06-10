/*
 *
 * ReprocessorRecipe.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.recipe.AbstractTwoToOneRecipe;
import it.zerono.mods.zerocore.lib.recipe.ingredient.FluidStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.ingredient.ItemStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.ItemStackRecipeResult;
import it.zerono.mods.zerocore.lib.recipe.serializer.TwoToOneRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.IntFunction;

public class ReprocessorRecipe
        extends AbstractTwoToOneRecipe<ItemStack, FluidStack, ItemStack,
                    ItemStackRecipeIngredient, FluidStackRecipeIngredient, ItemStackRecipeResult> {

    public static final String NAME = "reprocessor";
    public static final ResourceLocation ID = ExtremeReactors.ROOT_LOCATION.buildWithSuffix(NAME);
    public static final IntFunction<String> JSON_LABELS_SUPPLIER;

    protected ReprocessorRecipe(final ResourceLocation id, final ItemStackRecipeIngredient ingot,
                                final FluidStackRecipeIngredient fluid, final ItemStackRecipeResult result) {
        super(id, ingot, fluid, result, JSON_LABELS_SUPPLIER);
    }

    public boolean match(final ItemStack stack) {
        return this.getIngredient1().test(stack);
    }

    public boolean matchIgnoreAmount(final ItemStack stack) {
        return this.getIngredient1().testIgnoreAmount(stack);
    }

    public boolean matchIgnoreAmount(final FluidStack stack) {
        return this.getIngredient2().testIgnoreAmount(stack);
    }

    public static RecipeSerializer<ReprocessorRecipe> serializer() {
        return new TwoToOneRecipeSerializer<>(ReprocessorRecipe::new,
                ItemStackRecipeIngredient::from, ItemStackRecipeIngredient::from,
                FluidStackRecipeIngredient::from, FluidStackRecipeIngredient::from,
                ItemStackRecipeResult::from, ItemStackRecipeResult::from, JSON_LABELS_SUPPLIER);
    }

    //region AbstractTwoToOneRecipe

    @Override
    public RecipeSerializer<ReprocessorRecipe> getSerializer() {
        return Content.Recipes.REPROCESSOR_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Content.Recipes.REPROCESSOR_RECIPE_TYPE;
    }

    //endregion
    //region internals

    private static final String[] LABELS = {"waste", "fluid"};

    static {
        JSON_LABELS_SUPPLIER = n -> LABELS[n];
    }

    //endregion
}
