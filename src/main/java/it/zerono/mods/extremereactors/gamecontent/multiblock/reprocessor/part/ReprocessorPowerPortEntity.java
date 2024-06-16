/*
 *
 * ReprocessorPowerPortEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.MultiblockReprocessor;
import it.zerono.mods.zerocore.lib.energy.NullEnergyHandlers;
import it.zerono.mods.zerocore.lib.energy.adapter.ForgeEnergyAdapter;
import it.zerono.mods.zerocore.lib.energy.handler.WideEnergyStorageForwarder;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReprocessorPowerPortEntity
        extends AbstractReprocessorEntity {

    public ReprocessorPowerPortEntity(final BlockPos position, final BlockState blockState) {

        super(Content.TileEntityTypes.REPROCESSOR_POWERPORT.get(), position, blockState);
        this._forwarder = new WideEnergyStorageForwarder(NullEnergyHandlers.STORAGE);
        this._capability = LazyOptional.of(() -> ForgeEnergyAdapter.wrap(this._forwarder));
    }

    //region AbstractCuboidMultiblockPart

    @Override
    public void onPostMachineAssembled(MultiblockReprocessor controller) {

        super.onPostMachineAssembled(controller);
        this._forwarder.setHandler(this.getEnergyStorage());
    }

    @Override
    public void onPostMachineBroken() {

        super.onPostMachineBroken();
        this._forwarder.setHandler(NullEnergyHandlers.STORAGE);
    }

    @Override
    public boolean isGoodForPosition(final PartPosition position, final IMultiblockValidator validatorCallback) {
        return position.isVerticalFace() || super.isGoodForPosition(position, validatorCallback);
    }

    //endregion
    //region TileEntity

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CAPAP_FORGE_ENERGYSTORAGE == cap ? this._capability.cast() : super.getCapability(cap, side);
    }

    //endregion
    //region internals

    @SuppressWarnings("FieldMayBeFinal")
    private static Capability<IEnergyStorage> CAPAP_FORGE_ENERGYSTORAGE = CapabilityManager.get(new CapabilityToken<>(){});

    private final WideEnergyStorageForwarder _forwarder;
    private final LazyOptional<IEnergyStorage> _capability;

    //endregion
}
