/*
 *
 * Reactant.java
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

package it.zerono.mods.extremereactors.api.reactor;

import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.internal.AbstractNamedValue;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Describe the properties of a reactant, ie a material that can be used (Fuel) inside a
 * Reactor Fuel Rod to generate a controlled (or maybe not) reaction and that will eventually
 * produce a second reactant (Waste)
 */
public class Reactant
    extends AbstractNamedValue
    implements Predicate<ReactantType> {

    /**
     * Construct a new Reactant
     *
     * @param name The name of this reactant. Must be unique.
     * @param type The type of this reactant: Fuel or Waste.
     * @param rgbColour The color (in 0xRRGGBB form) to use when rendering fuel rods with this reactant in it.
     * @param fuelData The Fuel data associated to this Reactant (if it is a fuel)
     * @param translationKey The translation key for the name of the reactant.
     */
    Reactant(String name, ReactantType type, int rgbColour, String translationKey, FuelProperties fuelData) {

        super(name, translationKey);
        this._type = type;
        this._colour = Colour.fromRGB(rgbColour);
        this._fuelData = type.isFuel() ? Objects.requireNonNull(fuelData) : FuelProperties.INVALID;
    }

    public ReactantType getType() {
        return this._type;
    }

    public Colour getColour() {
        return this._colour;
    }

    public FuelProperties getFuelData() {
        return this._fuelData;
    }

    /**
     * Compute the minimum amount of this Reactant that can be produced from a solid source.
     *
     * @return The smallest amount of reactant found in a given reactant<>solid mapping set or -1 if no mapping could be found.
     */
    public int getMinimumSolidSourceAmount() {
        return ReactantMappingsRegistry.getToSolid(this)
                .orElseGet(Collections::emptyList)
                .stream()
                .mapToInt(IMapping::getSourceAmount)
                .reduce(Integer::min)
                .orElse(-1);
    }

    /**
     * Compute the minimum amount of this Reactant that can be produced from a fluid source.
     *
     * @return The smallest amount of reactant found in a given reactant<>fluid mapping set or -1 if no mapping could be found.
     */
    public int getMinimumFluidSourceAmount() {
        return ReactantMappingsRegistry.getToFluid(this)
                .map($ -> ReactantMappingsRegistry.STANDARD_FLUID_REACTANT_AMOUNT)
                .orElse(-1);
    }

    public Reactant copy() {
        return new Reactant(this.getName(), this.getType(), this.getColour().toRGBA(), this.getTranslationKey(), this.getFuelData());
    }

    //region Predicate<ReactantType>

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param type the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(final ReactantType type) {
        return this.getType() == type;
    }

    //endregion
    //region Object

    @Override
    public boolean equals(final Object obj) {

        if ((obj instanceof Reactant) && super.equals(obj)) {

            final Reactant other = (Reactant)obj;

            return this.test(other.getType()) &&
                    this.getColour() == other.getColour();
        }

        return false;
    }

    //endregion
    //region internals

    private final ReactantType _type;
    private final Colour _colour;
    private final FuelProperties _fuelData;

    //endregion
}
