/*
 * EnergizerComputerPortEntity
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part;

import dan200.computercraft.api.peripheral.IPeripheral;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.MultiBlockEnergizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.computer.EnergizerComputerPeripheral;
import it.zerono.mods.zerocore.lib.compat.Mods;
import it.zerono.mods.zerocore.lib.compat.computer.ConnectorComputerCraft;
import it.zerono.mods.zerocore.lib.compat.computer.MultiblockComputerPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergizerComputerPortEntity
        extends AbstractEnergizerEntity {

    public EnergizerComputerPortEntity(BlockPos position, BlockState blockState) {

        super(Content.TileEntityTypes.ENERGIZER_COMPUTERPORT.get(), position, blockState);

        this._ccConnector = Mods.COMPUTERCRAFT
                .map(() -> LazyOptional.of(() -> ConnectorComputerCraft.create("BigReactors-Energizer", this.getPeripheral())))
                .orElse(null);
        // TODO OC
    }

    //region ISyncableEntity

    /**
     * Sync the entity data from the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to read from
     * @param syncReason the reason why the synchronization is necessary
     */
    public void syncDataFrom(final CompoundTag data, final SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);

        if (null != this._ccConnector) {
            this._ccConnector.ifPresent(c -> c.syncDataFrom(data, syncReason));
        }
    }

    /**
     * Sync the entity data to the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundTag} the data was written to (usually {@code data})
     */
    public CompoundTag syncDataTo(final CompoundTag data, final SyncReason syncReason) {

        super.syncDataTo(data, syncReason);

        if (null != this._ccConnector) {
            this._ccConnector.ifPresent(c -> c.syncDataTo(data, syncReason));
        }

        return data;
    }

    //endregion
    //region AbstractReactorEntity

    @Override
    public void onAttached(MultiBlockEnergizer newController) {

        super.onAttached(newController);

        if (null != this._ccConnector) {
            //noinspection Convert2MethodRef
            this._ccConnector.ifPresent(c -> c.onAttachedToController());
        }
    }

    @Override
    public void onDetached(MultiBlockEnergizer oldController) {

        super.onDetached(oldController);

        if (null != this._ccConnector) {
            //noinspection Convert2MethodRef
            this._ccConnector.ifPresent(c -> c.onDetachedFromController());
        }
    }

    //endregion
    //region TileEntity

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {

        if (!this.isRemoved() && (null != this._ccConnector) && CAPABILITY_CC_PERIPHERAL == capability) {
            return this._ccConnector.cast();
        }

        return super.getCapability(capability, side);
    }

    //endregion
    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {
        return this.isMachineAssembled() ? 1 : 0;
    }

    //endregion
    //region internals

    private EnergizerComputerPeripheral getPeripheral() {

        if (null == this._peripheral) {
            this._peripheral = new EnergizerComputerPeripheral(this);
        }

        return this._peripheral;
    }

    @SuppressWarnings("FieldMayBeFinal")
    public static Capability<IPeripheral> CAPABILITY_CC_PERIPHERAL = CapabilityManager.get(new CapabilityToken<>(){});

    private final LazyOptional<ConnectorComputerCraft<MultiblockComputerPeripheral<MultiBlockEnergizer, EnergizerComputerPortEntity>>> _ccConnector;

    private EnergizerComputerPeripheral _peripheral;

    //endregion
}
