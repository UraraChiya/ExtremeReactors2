/*
 *
 * ItemGroups.java
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

package it.zerono.mods.extremereactors.gamecontent;

import com.google.common.collect.ImmutableList;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.item.ModItemGroup;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public final class ItemGroups {

    public static final CreativeModeTab GENERAL = new ModItemGroup(ExtremeReactors.MOD_ID + ".general",
            () -> stack(Content.Items.YELLORITE_ORE_BLOCK),
            () -> {

                final ImmutableList.Builder<ItemStack> builder = ImmutableList.<ItemStack>builder().add(
                        stack(Content.Blocks.YELLORITE_ORE_BLOCK), stack(Content.Blocks.ANGLESITE_ORE_BLOCK), stack(Content.Blocks.BENITOITE_ORE_BLOCK),
                        stack(Content.Items.YELLORIUM_INGOT), stack(Content.Items.YELLORIUM_DUST), stack(Content.Blocks.YELLORIUM_BLOCK),
                        stack(Content.Items.BLUTONIUM_INGOT), stack(Content.Items.BLUTONIUM_DUST), stack(Content.Blocks.BLUTONIUM_BLOCK),
                        stack(Content.Items.CYANITE_INGOT), stack(Content.Items.CYANITE_DUST), stack(Content.Blocks.CYANITE_BLOCK),
                        stack(Content.Items.MAGENTITE_INGOT), stack(Content.Items.MAGENTITE_DUST), stack(Content.Blocks.MAGENTITE_BLOCK),
                        stack(Content.Items.GRAPHITE_INGOT), stack(Content.Items.GRAPHITE_DUST), stack(Content.Blocks.GRAPHITE_BLOCK),
                        stack(Content.Items.LUDICRITE_INGOT), stack(Content.Items.LUDICRITE_DUST), stack(Content.Blocks.LUDICRITE_BLOCK),
                        stack(Content.Items.RIDICULITE_INGOT), stack(Content.Items.RIDICULITE_DUST), stack(Content.Blocks.RIDICULITE_BLOCK),
                        stack(Content.Items.INANITE_INGOT), stack(Content.Items.INANITE_DUST), stack(Content.Blocks.INANITE_BLOCK),
                        stack(Content.Items.YELLORIUM_NUGGET), stack(Content.Items.BLUTONIUM_NUGGET),
                        stack(Content.Items.ANGLESITE_CRYSTAL), stack(Content.Items.BENITOITE_CRYSTAL),
                        stack(Content.Items.WRENCH));

//                Mods.PATCHOULI.map(() -> PatchouliAPI.get().getBookStack(PatchouliCompat.HANDBOOK_ID)).ifPresent(builder::add);

                builder.add(stack(Content.Blocks.REPROCESSOR_CASING), stack(Content.Blocks.REPROCESSOR_GLASS),
                        stack(Content.Blocks.REPROCESSOR_CONTROLLER), stack(Content.Blocks.REPROCESSOR_COLLECTOR),
                        stack(Content.Blocks.REPROCESSOR_WASTEINJECTOR), stack(Content.Blocks.REPROCESSOR_FLUIDINJECTOR),
                        stack(Content.Blocks.REPROCESSOR_OUTPUTPORT), stack(Content.Blocks.REPROCESSOR_POWERPORT));

                builder.add(stack(Content.Items.STEAM_BUCKET), stack(Content.Items.YELLORIUM_BUCKET), stack(Content.Items.CYANITE_BUCKET),
                        stack(Content.Items.BLUTONIUM_BUCKET), stack(Content.Items.MAGENTITE_BUCKET), stack(Content.Items.VERDERIUM_BUCKET),
                        stack(Content.Items.ROSSINITE_BUCKET), stack(Content.Items.CRYOMISI_BUCKET), stack(Content.Items.TANGERIUM_BUCKET),
                        stack(Content.Items.REDFRIGIUM_BUCKET));

                builder.add(stack(Content.Blocks.FLUIDIZER_CASING), stack(Content.Blocks.FLUIDIZER_GLASS),
                        stack(Content.Blocks.FLUIDIZER_CONTROLLER), stack(Content.Blocks.FLUIDIZER_SOLIDINJECTOR),
                        stack(Content.Blocks.FLUIDIZER_FLUIDINJECTOR), stack(Content.Blocks.FLUIDIZER_OUTPUTPORT),
                        stack(Content.Blocks.FLUIDIZER_POWERPORT));

                return builder.build();
            });

    public static final CreativeModeTab REACTOR = new ModItemGroup(ExtremeReactors.MOD_ID + ".reactor",
            () -> stack(Content.Blocks.REACTOR_FUELROD_BASIC),
            () -> ImmutableList.of(
                    stack(Content.Blocks.REACTOR_CONTROLLER_BASIC), stack(Content.Blocks.REACTOR_CASING_BASIC),
                    stack(Content.Blocks.REACTOR_GLASS_BASIC), stack(Content.Blocks.REACTOR_FUELROD_BASIC),
                    stack(Content.Blocks.REACTOR_CONTROLROD_BASIC), stack(Content.Blocks.REACTOR_SOLID_ACCESSPORT_BASIC),
                    stack(Content.Blocks.REACTOR_CHARGINGPORT_FE_BASIC),
                    stack(Content.Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC), stack(Content.Blocks.REACTOR_POWERTAP_FE_PASSIVE_BASIC),
                    stack(Content.Blocks.REACTOR_REDSTONEPORT_BASIC),

                    stack(Content.Blocks.REACTOR_CONTROLLER_REINFORCED), stack(Content.Blocks.REACTOR_CASING_REINFORCED),
                    stack(Content.Blocks.REACTOR_GLASS_REINFORCED), stack(Content.Blocks.REACTOR_FUELROD_REINFORCED),
                    stack(Content.Blocks.REACTOR_CONTROLROD_REINFORCED), stack(Content.Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED),
                    stack(Content.Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED),
                    stack(Content.Blocks.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED), stack(Content.Blocks.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED),
                    stack(Content.Blocks.REACTOR_CHARGINGPORT_FE_REINFORCED),
                    stack(Content.Blocks.REACTOR_REDSTONEPORT_REINFORCED), stack(Content.Blocks.REACTOR_COMPUTERPORT_REINFORCED),
                    stack(Content.Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED), stack(Content.Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED),
                    stack(Content.Blocks.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED),
                    stack(Content.Blocks.REACTOR_CREATIVE_WATER_GENERATOR_REINFORCED)
            ));

    public static final CreativeModeTab TURBINE = new ModItemGroup(ExtremeReactors.MOD_ID + ".turbine",
            () -> stack(Content.Blocks.TURBINE_ROTORSHAFT_BASIC),
            () -> ImmutableList.of(
                    stack(Content.Blocks.TURBINE_CONTROLLER_BASIC), stack(Content.Blocks.TURBINE_CASING_BASIC),
                    stack(Content.Blocks.TURBINE_GLASS_BASIC), stack(Content.Blocks.TURBINE_ROTORBEARING_BASIC),
                    stack(Content.Blocks.TURBINE_ROTORSHAFT_BASIC), stack(Content.Blocks.TURBINE_ROTORBLADE_BASIC),
                    stack(Content.Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC), stack(Content.Blocks.TURBINE_POWERTAP_FE_PASSIVE_BASIC),
                    stack(Content.Blocks.TURBINE_CHARGINGPORT_FE_BASIC),
                    stack(Content.Blocks.TURBINE_REDSTONEPORT_BASIC),
                    stack(Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC), stack(Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC),
                    stack(Content.Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_BASIC),

                    stack(Content.Blocks.TURBINE_CONTROLLER_REINFORCED), stack(Content.Blocks.TURBINE_CASING_REINFORCED),
                    stack(Content.Blocks.TURBINE_GLASS_REINFORCED), stack(Content.Blocks.TURBINE_ROTORBEARING_REINFORCED),
                    stack(Content.Blocks.TURBINE_ROTORSHAFT_REINFORCED), stack(Content.Blocks.TURBINE_ROTORBLADE_REINFORCED),
                    stack(Content.Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED), stack(Content.Blocks.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED),
                    stack(Content.Blocks.TURBINE_CHARGINGPORT_FE_REINFORCED),
                    stack(Content.Blocks.TURBINE_REDSTONEPORT_REINFORCED), stack(Content.Blocks.TURBINE_COMPUTERPORT_REINFORCED),
                    stack(Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED), stack(Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED),
                    stack(Content.Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_REINFORCED)
            ));

    //region internals

    private static <T extends ItemLike> ItemStack stack(final Supplier<T> supplier) {
        return ItemHelper.stackFrom(supplier);
    }

    //endregion
}
