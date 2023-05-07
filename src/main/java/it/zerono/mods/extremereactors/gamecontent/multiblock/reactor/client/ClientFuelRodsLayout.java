/*
 *
 * ClientFuelRodsLayout.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.zerono.mods.extremereactors.api.reactor.Reactant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.FuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IFuelContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorFuelRodModel;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorFuelRodModelData;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFuelRodEntity;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class ClientFuelRodsLayout
        extends FuelRodsLayout {

    public ClientFuelRodsLayout(final Direction direction, final int length) {

        super(direction, length);

        this._assemblyFuelQuota = this._assemblyWasteQuota = 0.0f;
        this._fuelColor = this._wasteColor = Colour.WHITE;

        if (this.isVertical()) {

            this._rodsFuelData = new FuelData[this.getRodLength()];
            Arrays.setAll(this._rodsFuelData, idx -> new FuelData(Direction.Axis.Y));

        } else {

            this._rodsFuelData = new FuelData[1];
            this._rodsFuelData[0] = new FuelData(this.getAxis());
        }
    }

    public FuelData getFuelData(final int fuelRodIndex) {

        if (this.isVertical()) {
            return fuelRodIndex >= 0 && fuelRodIndex < this._rodsFuelData.length ? this._rodsFuelData[fuelRodIndex] : FuelData.EMPTY;
        } else {
            return this._rodsFuelData[0];
        }
    }

    public Colour getFuelColor() {
        return this._fuelColor;
    }

    public Colour getWasteColor() {
        return this._wasteColor;
    }

    public Colour getModelTint(final int tintIndex) {

        switch (tintIndex) {

            case 0:
                return this.getFuelColor();

            case 1:
                return this.getWasteColor();

            default:
                return Colour.WHITE;
        }
    }

    //region FuelRodsLayout

    @Override
    public IntSet updateFuelData(final IFuelContainer fuelContainer, int fuelRodsInReactor) {

        super.updateFuelData(fuelContainer, fuelRodsInReactor);

        // fuel/waste colors
        final boolean reactantsChanged = this.updateGfx(fuelContainer);

        // fuel/waste quota for each fuel rod
        this.updateQuotas(fuelContainer, fuelRodsInReactor);

        // split fuel/waste between fuel rods

        if (this.isVertical()) {
            // vertical column, fluids pool to the bottom (waste first)
            return this.updateFuelDataVertically(reactantsChanged);
        } else {
            // horizontal column, fluids distribute equally
            return this.updateFuelDataHorizontally(reactantsChanged);
        }
    }

    @Override
    public void updateFuelRodsOcclusion(final Level world, final Iterable<ReactorFuelRodEntity> fuelRods, final boolean interiorInvisible) {

        if (interiorInvisible) {

            fuelRods.forEach(r -> r.setOccluded(true));
            return;
        }

        final Direction[] directions = this.getRadiateDirections();
        final RenderType solid = RenderType.solid();

        for (final ReactorFuelRodEntity fuelRod : fuelRods) {

            final BlockPos rodPosition = fuelRod.getWorldPosition();
            boolean occluded = true;

            for (final Direction direction : directions) {

                final BlockPos checkPosition = rodPosition.relative(direction);
                final BlockState state = world.getBlockState(checkPosition);

                if (state.isAir() || !ItemBlockRenderTypes.canRenderInLayer(state, solid)) {

                    occluded = false;
                    break;
                }
            }

            fuelRod.setOccluded(occluded);
        }
    }

    private IntSet updateFuelDataVertically(final boolean reactantsChanged) {

        // vertical column, reactants pool to the bottom (waste first)

        final IntSet updatedIndices = new IntArraySet(this.getRodLength());
        final float rodCapacity = ReactorFuelRodEntity.FUEL_CAPACITY_PER_FUEL_ROD;
        float remainingWaste = this._assemblyWasteQuota;
        float remainingFuel = this._assemblyFuelQuota;
        float fuelAmount, wasteAmount;

        for (int i = 0; i < this.getRodLength(); ++i) {

            if (remainingWaste > 0.0f) {

                wasteAmount = Math.min(remainingWaste, rodCapacity);
                remainingWaste -= wasteAmount;

            } else {

                wasteAmount = 0.0f;
            }

            if (remainingFuel > 0.0f) {

                fuelAmount = Math.min(remainingFuel, rodCapacity - wasteAmount);
                remainingFuel -= fuelAmount;

            } else {

                fuelAmount = 0.0f;
            }

            if (this.getFuelData(i).update(fuelAmount, wasteAmount)) {
                updatedIndices.add(i);
            } else if (reactantsChanged && (fuelAmount > 0 || wasteAmount > 0)) {
                updatedIndices.add(i);
            }
        }

        return updatedIndices;
    }

    private IntSet updateFuelDataHorizontally(final boolean reactantsChanged) {

        // horizontal column, reactants distribute equally
        if (this.getFuelData(0).update(this._assemblyFuelQuota / this.getRodLength(), this._assemblyWasteQuota / this.getRodLength())) {
            return ALL_CHANGED;
        }

        if (reactantsChanged) {
            return ALL_CHANGED;
        }

        return IntSets.EMPTY_SET;
    }

    private boolean isVertical() {
        return Direction.Plane.VERTICAL == this.getOrientation();
    }

    @Override
    public ReactorFuelRodModelData getFuelRodModelData(int rodIndex, boolean rodOccluded) {
        return ReactorFuelRodModelData.from(this.getFuelData(rodIndex), rodOccluded);
    }

    @Override
    public void reset() {

        this._fuelColor = this._wasteColor = Colour.WHITE;
        this._assemblyFuelQuota = this._assemblyWasteQuota = 0f;
        Arrays.stream(this._rodsFuelData).forEach(FuelData::reset);
    }

    //endregion
    //region FuelData

    public static class FuelData {

        public static final FuelData EMPTY = new FuelData(Direction.Axis.X);

        FuelData(Direction.Axis orientation) {

            this._orientation = orientation;
            this._fuelLevel = this._wasteLevel = 0;
        }

        public byte getFuelLevel() {
            return this._fuelLevel;
        }

        public byte getWasteLevel() {
            return this._wasteLevel;
        }

        public boolean isVertical() {
            return this.getOrientation().isVertical();
        }

        public Direction.Axis getOrientation() {
            return this._orientation;
        }

        public void reset() {
            this._fuelLevel = this._wasteLevel = 0;
        }

        //region Object

        @Override
        public String toString() {
            return String.format("Fuel lvl=%d, Waste lvl=%d, Orientation=%s", this._fuelLevel, this._wasteLevel, this._orientation);
        }

        //endregion
        //region internals

        private boolean update(final float fuelAmount, final float wasteAmount) {

            if (this == EMPTY) {
                return false;
            }

            final float levelStep = this._orientation.isVertical() ? VERTICAL_LEVEL_STEP : HORIZONTAL_LEVEL_STEP;
            final byte newFuelLevel = (byte)Math.round(fuelAmount / levelStep);
            final byte newWasteLevel = (byte)Math.round(wasteAmount / levelStep);
            final boolean changed = this._fuelLevel != newFuelLevel || this._wasteLevel != newWasteLevel;

            if (changed) {

                this._fuelLevel = newFuelLevel;
                this._wasteLevel = newWasteLevel;
            }

            return changed;
        }

        private byte _fuelLevel; /* level is 0-12 or 0-10 depending on layout orientation */
        private byte _wasteLevel;
        private final Direction.Axis _orientation;

        //endregion
    }

    //endregion
    //region internals

    private void setAssemblyFuelQuota(final float quota) {
        this._assemblyFuelQuota = verifyFloat(quota);
    }

    private void setAssemblyWasteQuota(final float quota) {
        this._assemblyWasteQuota = verifyFloat(quota);
    }

    private static float verifyFloat(float value) {
        return Float.isNaN(value) || Float.isInfinite(value) ? 0f : value;
    }

    private static float computeAssemblyReactantQuota(int reactantAmount, int fuelRodsInAssembly, int fuelRodsInReactor) {
        return (float) reactantAmount / (float) fuelRodsInReactor * (float) fuelRodsInAssembly;
    }

    private void updateQuotas(final IFuelContainer fuelContainer, int fuelRodsInReactor) {

        fuelRodsInReactor = Math.max(1, fuelRodsInReactor);

        this.setAssemblyFuelQuota(computeAssemblyReactantQuota(fuelContainer.getFuelAmount(), this.getRodLength(), fuelRodsInReactor));
        this.setAssemblyWasteQuota(computeAssemblyReactantQuota(fuelContainer.getWasteAmount(), this.getRodLength(), fuelRodsInReactor));
    }

    private boolean updateGfx(final IFuelContainer fuelContainer) {

        final Colour oldFuelColor = this.getFuelColor();
        final Colour oldWasteColor = this.getWasteColor();

        this._fuelColor = fuelContainer.getFuel().map(Reactant::getColour).orElse(Colour.WHITE);
        this._wasteColor = fuelContainer.getWaste().map(Reactant::getColour).orElse(Colour.WHITE);
        return !this.getFuelColor().equals(oldFuelColor) || !this.getWasteColor().equals(oldWasteColor);
    }

    private static final float HORIZONTAL_LEVEL_STEP = ReactorFuelRodEntity.FUEL_CAPACITY_PER_FUEL_ROD / (float)ReactorFuelRodModel.HORIZONTAL_MAX_STEPS;
    private static final float VERTICAL_LEVEL_STEP = ReactorFuelRodEntity.FUEL_CAPACITY_PER_FUEL_ROD / (float)ReactorFuelRodModel.VERTICAL_MAX_STEPS;
    private static final IntSet ALL_CHANGED = IntSets.singleton(-1);

    private final FuelData[] _rodsFuelData;
    private float _assemblyFuelQuota;
    private float _assemblyWasteQuota;

    private Colour _fuelColor;
    private Colour _wasteColor;

    //endregion
}
