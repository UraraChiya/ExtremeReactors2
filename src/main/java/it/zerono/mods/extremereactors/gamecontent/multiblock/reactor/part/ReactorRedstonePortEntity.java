/*
 *
 * ReactorRedstonePortEntity.java
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

import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.sensor.ReactorSensorSetting;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.multiblock.ITickableMultiblockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ReactorRedstonePortEntity
        extends AbstractReactorEntity
        implements INeighborChangeListener, ITickableMultiblockPart, MenuProvider {

    public ReactorRedstonePortEntity(final BlockPos position, final BlockState blockState) {

        super(Content.TileEntityTypes.REACTOR_REDSTONEPORT.get(), position, blockState);
        this._setting = ReactorSensorSetting.DISABLED;
        this._isExternallyPowered = false;
        this._externalPowerLevel = 0;
        this._ticksSinceLastUpdate = 0;
        this._isLit = false;

        this.setCommandDispatcher(TileCommandDispatcher.<ReactorRedstonePortEntity>builder()
                .addServerHandler(CommonConstants.COMMAND_SET_REDSTONE_SENSOR, ReactorRedstonePortEntity::setNewSensorFromGUI)
                .addServerHandler(CommonConstants.COMMAND_DISABLE_REDSTONE_SENSOR, ReactorRedstonePortEntity::disableSensorFromGUI)
                .build(this));
    }

    public ReactorSensorSetting getSettings() {
        return this._setting;
    }

    /**
     * @return the level of power emitted by this port
     */
    public int getOutputSignalPower() {
        return this.getSettings().Sensor.isOutput() && this.isRedstoneActive() ? 15 : 0;
    }

    /**
     * @return true if the port is receiving or emitting a redstone signal, false otherwise
     */
    public boolean isLit() {
        return this._isLit;
    }

    //region INeighborChangeListener

    /**
     * Called when a neighboring Block on a side of this TileEntity changes
     *
     * @param state the BlockState of this TileEntity block
     * @param neighborPosition position of neighbor
     */
    @Override
    public void onNeighborBlockChanged(final BlockState state, final BlockPos neighborPosition, final boolean isMoving) {

        if (!this.isConnected()) {
            return;
        }

        if (this.getSettings().Sensor.isInput()) {

            this.getOutwardDirection()
                    .map(direction -> this.getRedstonePowerLevelFrom(this.getWorldPosition().relative(direction), direction))
                    .ifPresent(powerLevel -> {

                        final boolean nowPowered = powerLevel > 0;

                        if (this._isExternallyPowered != nowPowered || this._externalPowerLevel != powerLevel) {

                            this._isExternallyPowered = nowPowered;
                            this._externalPowerLevel = powerLevel;
                            this.onRedstoneInputUpdated();
                            this.setChanged();
                            this.updateRedstoneStateAndNotify();
                        }
                    });

        } else {

            this._isExternallyPowered = false;
            this._externalPowerLevel = 0;
        }
    }

    //endregion
    //region ITickableMultiblockPart

    /**
     * Updates the redstone block's status, if it's an output network, if there is one.
     * Will only send one update per N ticks, where N is a configurable setting.
     */
    public void onMultiblockServerTick() {

        if (!this.isConnected() || (this._ticksSinceLastUpdate++ < Config.COMMON.general.ticksPerRedstoneUpdate.get())) {
            return;
        }

        this.updateRedstoneStateAndNotify();
        this._ticksSinceLastUpdate = 0;
    }

    //endregion
    //region MenuProvider

    /**
     * Create the SERVER-side container for this TileEntity
     * @param windowId  the window id
     * @param inventory the player inventory
     * @param player    the player
     * @return the container to use on the server
     */
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory inventory, final Player player) {
        return ModTileContainer.empty(Content.ContainerTypes.REACTOR_REDSTONEPORT.get(), windowId, this, (ServerPlayer)player);
    }

    @Override
    public Component getDisplayName() {
        return super.getPartDisplayName();
    }

    //endregion
    //region ISyncableEntity

    @Override
    public void syncDataFrom(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        super.syncDataFrom(data, registries, syncReason);

        if (data.contains("setting")) {
            this._setting = ReactorSensorSetting.syncDataFrom(data.getCompound("setting"));
        }

        if (data.contains("lit")) {
            this._isLit = data.getBoolean("lit");
        }

        if (syncReason.isFullSync()) {
            this.updateRedstoneStateAndNotify();
        } else {
            this.markForRenderUpdate();
        }
    }

    @Override
    public CompoundTag syncDataTo(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        super.syncDataTo(data, registries, syncReason);

        data.put("setting", this.getSettings().syncDataTo(new CompoundTag()));
        data.putBoolean("lit", this._isLit);

        return data;
    }

    //endregion
    //region AbstractReactorEntity

    @Override
    protected int getUpdatedModelVariantIndex() {
        return this._isLit ? 1 : 0;
    }

    //endregion
    //region AbstractCuboidMultiblockPart

    @Override
    public void onPostMachineAssembled(MultiblockReactor controller) {

        super.onPostMachineAssembled(controller);
        this.callOnLogicalServer(this::updateRedstoneStateAndNotify);
    }

    @Override
    public void onPostMachineBroken() {

        super.onPostMachineBroken();
        this.callOnLogicalServer(this::updateRedstoneStateAndNotify);
    }

    @Override
    public void onMachineActivated() {

        super.onMachineActivated();
        this.callOnLogicalServer(this::updateRedstoneStateAndNotify);
    }

    @Override
    public void onMachineDeactivated() {

        super.onMachineDeactivated();
        this.callOnLogicalServer(this::updateRedstoneStateAndNotify);
    }

    //endregion
    //region AbstractModBlockEntity

    /**
     * Check if the tile entity has a GUI or not
     * Override in derived classes to return true if your tile entity got a GUI
     */
    @Override
    public boolean canOpenGui(Level world, BlockPos position, BlockState state) {
        return this.isMachineAssembled();
    }

    //endregion
    //region internals
    //region Redstone methods

    protected boolean isRedstoneActive() {

        final ReactorSensorSetting settings = this.getSettings();

        return settings.Sensor.isOutput() ?
                this.getMultiblockController().map(settings::test).orElse(false) :
                this._isExternallyPowered && this.isConnected();
    }

    /**
     * Called to do business logic when the redstone value has changed
      */
    private void onRedstoneInputUpdated() {
        this.getMultiblockController().ifPresent(c -> this.getSettings().inputAction(c, this._isExternallyPowered, this._externalPowerLevel));
    }

    private void updateRedstoneStateAndNotify() {

        this.callOnLogicalServer(world -> {

            final boolean oldLitState = this._isLit;

            if (oldLitState != this.updateLitState()) {
                world.updateNeighborsAt(this.getWorldPosition(), this.getBlockType());
            }

            this.notifyTileEntityUpdate();
        });
    }

    /**
     * Command handler for setting a new sensor from the GUI
     * @param data parameters from the GUI
     */
    private void setNewSensorFromGUI(final CompoundTag data) {

        this._setting = ReactorSensorSetting.syncDataFrom(data);

        this.getOutwardDirection().ifPresent(outward -> {

            final BlockPos position = this.getWorldPosition();

            if (this._setting.Sensor.isInput()) {

                // Update inputs so we don't pulse/change automatically

                this._externalPowerLevel = this.getRedstonePowerLevelFrom(position.relative(outward), outward);
                this._isExternallyPowered = this._externalPowerLevel > 0;

                if (!this._setting.Behavior.onPulse()) {
                    this.onRedstoneInputUpdated();
                }

            } else {

                this._isExternallyPowered = false;
                this._externalPowerLevel = 0;
            }
        });

        this.updateRedstoneStateAndNotify();
    }

    private void disableSensorFromGUI() {

        this._setting = ReactorSensorSetting.DISABLED;
        this.updateRedstoneStateAndNotify();
    }

    /**
     * Call with the coordinates of the block to check and the direction
     * towards that block from your block.
     * If the block towards which this block is emitting power lies north,
     * then pass in south.
     */
    private boolean isReceivingRedstonePowerFrom(final BlockPos position, final Direction direction) {
        return this.mapPartWorld(w -> w.getBestNeighborSignal(position) > 0 || w.getSignal(position, direction) > 0, false);
    }

    /**
     * Call with the coordinates of the block to check and the direction
     * towards that block from your block.
     * If the block towards which this block is emitting power lies north,
     * then pass in south.
     */
    private int getRedstonePowerLevelFrom(final BlockPos position, final Direction direction) {
        return this.mapPartWorld(w -> Mth.clamp(w.getSignal(position, direction), 0, 15), 0);
    }

    /**
     * Update the "lit" state and return it
     * @return the lit state
     */
    protected boolean updateLitState() {
        return this._isLit = (this.getSettings().Sensor.isOutput() && this.isRedstoneActive()) || this._isExternallyPowered;
    }

    //endregion

    private ReactorSensorSetting _setting;
    private int _ticksSinceLastUpdate;
    private boolean _isLit;
    private boolean _isExternallyPowered;
    private int _externalPowerLevel;

    //endregion
}
