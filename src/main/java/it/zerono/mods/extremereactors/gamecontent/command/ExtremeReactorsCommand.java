/*
 *
 * ExtremeReactorsCommand.java
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

package it.zerono.mods.extremereactors.gamecontent.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.reactor.*;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ExtremeReactorsCommand {

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal("er")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.literal("reactants")
                        .then(Commands.literal("get")
                                .then(nameParam().executes(ExtremeReactorsCommand::getReactant))
                        )
                        .then(Commands.literal("set")
                                .then(nameParam()
                                        .then(stringCommand("colour", ExtremeReactorsCommand::setReactantColour))
                                        .then(floatCommand("moderation", 0.0f, context -> setReactantFuelValue(context,
                                                (Reactant r) -> r.getFuelData().getModerationFactor(),
                                                (Reactant r, Float v) -> r.getFuelData().setModerationFactor(v))))
                                        .then(floatCommand("absorption", 0.0f, 1.0f, context -> setReactantFuelValue(context,
                                                (Reactant r) -> r.getFuelData().getAbsorptionCoefficient(),
                                                (Reactant r, Float v) -> r.getFuelData().setAbsorptionCoefficient(v))))
                                        .then(floatCommand("hardness", 1.0f, context -> setReactantFuelValue(context,
                                                (Reactant r) -> r.getFuelData().getHardnessDivisor(),
                                                (Reactant r, Float v) -> r.getFuelData().setHardnessDivisor(v))))
                                        .then(floatCommand("fissionevents", 0.0f, context -> setReactantFuelValue(context,
                                                (Reactant r) -> r.getFuelData().getFissionEventsPerFuelUnit(),
                                                (Reactant r, Float v) -> r.getFuelData().setFissionEventsPerFuelUnit(v))))
                                        .then(floatCommand("fuelunits", 0.0f, context -> setReactantFuelValue(context,
                                                (Reactant r) -> r.getFuelData().getFuelUnitsPerFissionEvent(),
                                                (Reactant r, Float v) -> r.getFuelData().setFuelUnitsPerFissionEvent(v))))
                                )
                        )
                )
                .then(Commands.literal("reaction")
                        .then(Commands.literal("get")
                                .then(nameParam().executes(ExtremeReactorsCommand::getReaction))
                        )
                        .then(Commands.literal("set")
                                .then(nameParam()
                                        .then(floatCommand("reactivity", 1.0f, context -> setReactionValue(context, "_reactivity", getFloat(context))))
                                        .then(floatCommand("fissionRate", 0.0001f, context -> setReactionValue(context, "_fissionRate", getFloat(context))))
                                )
                        )
                )
        );
    }

    //region internals

    private ExtremeReactorsCommand() {
    }

    //region commands & parameters

    private static ArgumentBuilder<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>> stringCommand(final String propertyName,
                                                                                                                 final Command<CommandSourceStack> cmd) {
        return Commands.literal(propertyName).then(Commands.argument(PARAM_VALUE, StringArgumentType.string()).executes(cmd));
    }

    private static ArgumentBuilder<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>> floatCommand(final String propertyName,
                                                                                                                final float min,
                                                                                                                final Command<CommandSourceStack> cmd) {
        return Commands.literal(propertyName).then(Commands.argument(PARAM_VALUE, FloatArgumentType.floatArg(min)).executes(cmd));
    }

    private static ArgumentBuilder<CommandSourceStack, LiteralArgumentBuilder<CommandSourceStack>> floatCommand(final String propertyName,
                                                                                                                final float min, final float max,
                                                                                                                final Command<CommandSourceStack> cmd) {
        return Commands.literal(propertyName).then(Commands.argument(PARAM_VALUE, FloatArgumentType.floatArg(min, max)).executes(cmd));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> nameParam() {
        return Commands.argument(PARAM_NAME, StringArgumentType.string());
    }

    private static String getName(final CommandContext<CommandSourceStack> context) {
        return StringArgumentType.getString(context, PARAM_NAME);
    }

    private static String getString(final CommandContext<CommandSourceStack> context) {
        return StringArgumentType.getString(context, PARAM_VALUE);
    }

    private static float getFloat(final CommandContext<CommandSourceStack> context) {
        return FloatArgumentType.getFloat(context, PARAM_VALUE);
    }

    //endregion
    //region reactants

    private static int getReactant(final CommandContext<CommandSourceStack> context) {

        context.getSource().sendSuccess(ReactantsRegistry.get(getName(context))
                .map(ExtremeReactorsCommand::getTextFrom)
                .orElse(new TextComponent("Reactant not found")), true);
        return 0;
    }

    private static int setReactantColour(final CommandContext<CommandSourceStack> context) {

        context.getSource().sendSuccess(ReactantsRegistry.get(getName(context))
                .map(r -> setReactantColour(r, (int)Long.parseLong(getString(context), 16)))
                .orElse(new TextComponent("Reactant not found")), true);
        return 0;
    }

    private static Component setReactantColour(final Reactant reactant, final int colour) {

        try {

            final Field f = reactant.getClass().getDeclaredField("_colour");

            f.setAccessible(true);
            f.set(reactant, Colour.fromRGBA(colour));
            return new TextComponent(String.format("Reactant %s colour set to 0x%08X", reactant.getName(), colour));

        } catch (Exception ex) {

            Log.LOGGER.error(ex);
            return new TextComponent("Exception raised while setting colour field");
        }
    }

    private static int setReactantFuelValue(final CommandContext<CommandSourceStack> context, final Function<Reactant, Float> getter,
                                            final BiConsumer<Reactant, Float> setter) {

        context.getSource().sendSuccess(ReactantsRegistry.get(getName(context))
                .filter(r -> r.test(ReactantType.Fuel))
                .map(r -> setReactantFuelValue(r, getFloat(context), getter, setter))
                .orElse(new TextComponent("Fuel Reactant not found")), true);
        return 0;
    }

    private static Component setReactantFuelValue(final Reactant reactant, final float value,
                                                       final Function<Reactant, Float> getter,
                                                       final BiConsumer<Reactant, Float> setter) {

        setter.accept(reactant, value);
        return new TextComponent(String.format("Value set to %f", getter.apply(reactant)));
    }

    private static Component getTextFrom(final Reactant reactant) {

        final MutableComponent text = new TextComponent(String.format("[" +
                        ChatFormatting.BOLD + "%s" + ChatFormatting.RESET + "] " + ChatFormatting.GOLD + "%s; " + ChatFormatting.RESET +
                        ChatFormatting.ITALIC + "color: " + ChatFormatting.RESET + "%08X",
                reactant.getType(), reactant.getName(), reactant.getColour().toRGBA()));

        if (reactant.test(ReactantType.Fuel)) {

            final FuelProperties properties = reactant.getFuelData();

            text.append(new TextComponent(String.format("; " +
                            ChatFormatting.ITALIC + "moderation: " + ChatFormatting.RESET + "%f; " +
                            ChatFormatting.ITALIC + "absorption: " + ChatFormatting.RESET + "%f; " +
                            ChatFormatting.ITALIC + "hardness: " + ChatFormatting.RESET + "%f; " +
                            ChatFormatting.ITALIC + "fissionEventsPerFuelUnit: " + ChatFormatting.RESET + "%f; " +
                            ChatFormatting.ITALIC + "fuelUnitsPerFissionEvent: " + ChatFormatting.RESET + "%f",
                    properties.getModerationFactor(), properties.getAbsorptionCoefficient(), properties.getHardnessDivisor(),
                    properties.getFissionEventsPerFuelUnit(), properties.getFuelUnitsPerFissionEvent())));
        }

        return text;
    }

    //endregion
    //region reactions

    private static int getReaction(final CommandContext<CommandSourceStack> context) {

        context.getSource().sendSuccess(ReactantsRegistry.get(getName(context))
                .flatMap(ReactionsRegistry::get)
                .map(ExtremeReactorsCommand::getTextFrom)
                .orElse(new TextComponent("Reactant or reaction not found")), true);
        return 0;
    }

    private static Component getTextFrom(final Reaction reaction) {
        return new TextComponent(String.format("[" +
                ChatFormatting.BOLD + "%s" + ChatFormatting.RESET + " -> " +
                ChatFormatting.BOLD + "%s" + ChatFormatting.RESET + "] " +
                ChatFormatting.ITALIC + "reactivity: " + ChatFormatting.RESET + "%f; " +
                ChatFormatting.ITALIC + "fissionRate: " + ChatFormatting.RESET + "%f",
                reaction.getSource(), reaction.getProduct(), reaction.getReactivity(), reaction.getFissionRate()));
    }

    private static int setReactionValue(final CommandContext<CommandSourceStack> context, final String fieldName, final float value) {

        context.getSource().sendSuccess(ReactantsRegistry.get(getName(context))
                .flatMap(ReactionsRegistry::get)
                .map(reaction -> {
                    try {

                        final Field f = reaction.getClass().getDeclaredField(fieldName);

                        f.setAccessible(true);
                        f.set(reaction, value);
                        return new TextComponent(String.format("Reaction %s parameter %s set to %f", reaction.getSource(), fieldName, value));

                    } catch (Exception ex) {

                        Log.LOGGER.error(ex);
                        return new TextComponent(String.format("Exception raised while setting Reaction field %s", fieldName));
                    }
                })
                .orElse(new TextComponent("Reactant or reaction not found")), true);
        return 0;
    }

    //endregion

    private static final String PARAM_NAME = "name";
    private static final String PARAM_VALUE = "value";

    //endregion
}