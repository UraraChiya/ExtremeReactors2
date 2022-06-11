/*
 *
 * ReactorComputerPortEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part;

//import dan200.computercraft.api.peripheral.IPeripheral;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.computer.ReactorComputerPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.CapabilityManager;
//import net.minecraftforge.common.capabilities.CapabilityToken;
//import net.minecraftforge.common.util.LazyOptional;

//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;

public class ReactorComputerPortEntity
        extends AbstractReactorEntity {

    public ReactorComputerPortEntity(final BlockPos position, final BlockState blockState) {

        super(Content.TileEntityTypes.REACTOR_COMPUTERPORT.get(), position, blockState);

//        this._ccConnector = Mods.COMPUTERCRAFT
//                .map(() -> LazyOptional.of(() -> ConnectorComputerCraft.create("BigReactors-Reactor", this.getPeripheral())))
//                .orElse(null);
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

////        this.executeOnComputerCraftConnector(c -> c.syncDataFrom(data, syncReason));
//        if (null != this._ccConnector) {
//            this._ccConnector.ifPresent(c -> c.syncDataFrom(data, syncReason));
//        }
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

////        this.executeOnComputerCraftConnector(c -> c.syncDataTo(data, syncReason));
//        if (null != this._ccConnector) {
//            this._ccConnector.ifPresent(c -> c.syncDataTo(data, syncReason));
//        }

        return data;
    }

    //endregion
    //region AbstractReactorEntity

//    @Override
//    public void onAttached(MultiblockReactor newController) {
//
//        super.onAttached(newController);
////        this.executeOnComputerCraftConnector(Connector::onAttachedToController);
//
//        if (null != this._ccConnector) {
//            //noinspection Convert2MethodRef
//            this._ccConnector.ifPresent(c -> c.onAttachedToController());
//        }
//    }

//    @Override
//    public void onDetached(MultiblockReactor oldController) {
//
//        super.onDetached(oldController);
////        this.executeOnComputerCraftConnector(Connector::onDetachedFromController);
//        if (null != this._ccConnector) {
//            //noinspection Convert2MethodRef
//            this._ccConnector.ifPresent(c -> c.onDetachedFromController());
//        }
//    }

    //endregion
    //region TileEntity

//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
//
//        if (!this.isRemoved() && (null != this._ccConnector) && CAPABILITY_CC_PERIPHERAL == capability) {
//                return this._ccConnector.cast();
//        }
//
//        return super.getCapability(capability, side);
//    }

    //endregion
    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {
        return this.isMachineAssembled() ? 1 : 0;
    }

    //endregion
    //region internals

    private ReactorComputerPeripheral getPeripheral() {

        if (null == this._peripheral) {
            this._peripheral = new ReactorComputerPeripheral(this);
        }

        return this._peripheral;
    }

//    private void executeOnComputerCraftConnector(final NonNullConsumer<ConnectorComputerCraft<MultiblockComputerPeripheral<MultiblockReactor, ReactorComputerPortEntity>>> c) {
//
//        if (null != this._ccConnector) {
//            this._ccConnector.ifPresent(c);
//        }
//    }

//    @SuppressWarnings("FieldMayBeFinal")
//    public static Capability<IPeripheral> CAPABILITY_CC_PERIPHERAL = CapabilityManager.get(new CapabilityToken<>(){});
//
//    private final LazyOptional<ConnectorComputerCraft<MultiblockComputerPeripheral<MultiblockReactor, ReactorComputerPortEntity>>> _ccConnector;

    private ReactorComputerPeripheral _peripheral;

    //endregion
}
