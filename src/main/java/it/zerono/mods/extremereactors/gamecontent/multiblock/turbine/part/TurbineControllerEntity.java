/*
 *
 * TurbineControllerEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part;

import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.model.data.ModelTransformers;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.VentSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.container.TurbineControllerContainer;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher;
import it.zerono.mods.zerocore.lib.data.nbt.NBTHelper;
import it.zerono.mods.zerocore.lib.network.INetworkTileEntitySyncProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

public class TurbineControllerEntity
        extends AbstractTurbineEntity
        implements MenuProvider, INetworkTileEntitySyncProvider {

    public static String COMMAND_ENGAGE_COILS = "coilon";
    public static String COMMAND_DISENGAGE_COILS = "coiloff";
    public static String COMMAND_SET_INTAKERATE = "rate";
    public static String COMMAND_SET_VENT = "vent";
    public static String COMMAND_SCRAM = "scram";

    public TurbineControllerEntity(final BlockPos position, final BlockState blockState) {

        super(Content.TileEntityTypes.TURBINE_CONTROLLER.get(), position, blockState);

        this.setCommandDispatcher(TileCommandDispatcher.<TurbineControllerEntity>builder()
                .addServerHandler(CommonConstants.COMMAND_ACTIVATE, tce -> tce.setTurbineActive(true))
                .addServerHandler(CommonConstants.COMMAND_DEACTIVATE, tce -> tce.setTurbineActive(false))
                .addServerHandler(COMMAND_ENGAGE_COILS, tce -> tce.executeOnController(tc -> tc.setInductorEngaged(true)))
                .addServerHandler(COMMAND_DISENGAGE_COILS, tce -> tce.executeOnController(tc -> tc.setInductorEngaged(false)))
                .addServerHandler(COMMAND_SET_INTAKERATE, TurbineControllerEntity::setIntakeRate)
                .addServerHandler(COMMAND_SET_VENT, TurbineControllerEntity::setVent)
                .addServerHandler(COMMAND_SCRAM, TurbineControllerEntity::scram) // TODO ?
                .build(this)
        );
    }

    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {

        return (byte) (this.isMachineAssembled() ?
                (this.isTurbineActive() ? ModelTransformers.MODEL_VARIANT_1 : ModelTransformers.MODEL_VARIANT_2) :
                ModelTransformers.MODEL_DEFAULT);
    }

    //endregion
    //region IMultiblockPart

    @Override
    public void onPostMachineAssembled(MultiblockTurbine controller) {

        super.onPostMachineAssembled(controller);
        this.listenForControllerDataUpdates();
    }

    /**
     * Called when the user activates the machine. This is not called by default, but is included
     * as most machines have this game-logical concept.
     */
    @Override
    public void onMachineActivated() {

        super.onMachineActivated();
        this.requestClientRenderUpdate();
    }

    /**
     * Called when the user deactivates the machine. This is not called by default, but is included
     * as most machines have this game-logical concept.
     */
    @Override
    public void onMachineDeactivated() {

        super.onMachineDeactivated();
        this.requestClientRenderUpdate();
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        super.getDebugMessages(side, messages);
        this.getMultiblockController().ifPresent(reactor -> reactor.getDebugMessages(side, messages));
    }

    //endregion
    //region AbstractModBlockEntity

    @Override
    public boolean canOpenGui(Level world, BlockPos position, BlockState state) {
        return this.isMachineAssembled();
    }

    //endregion
    //region INetworkTileEntitySyncProvider

    /**
     * Add the player to the update queue.
     *
     * @param player    the player to send updates to.
     * @param updateNow if true, send an update to the player immediately.
     */
    @Override
    public void enlistForUpdates(ServerPlayer player, boolean updateNow) {
        this.getMultiblockController().ifPresent(c -> c.enlistForUpdates(player, updateNow));
    }

    /**
     * Remove the player for the update queue.
     *
     * @param player the player to be removed from the update queue.
     */
    @Override
    public void delistFromUpdates(ServerPlayer player) {
        this.getMultiblockController().ifPresent(c -> c.delistFromUpdates(player));
    }

    /**
     * Send an update to all enlisted players
     */
    @Override
    public void sendUpdates() {
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
        return new TurbineControllerContainer(windowId, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return super.getPartDisplayName();
    }

    //endregion
    //region internals
    //region Tile Commands

    private void setIntakeRate(CompoundTag data) {
        this.executeOnController(turbine -> turbine.setMaxIntakeRate(data.getInt("rate")));
    }

    private void setVent(CompoundTag data) {

        final VentSetting setting = NBTHelper.nbtGetEnum(data, "vent", VentSetting::valueOf, VentSetting.getDefault());

        this.executeOnController(turbine -> turbine.setVentSetting(setting));
    }

    private void scram() {
        this.setTurbineActive(false);
    }

    //endregion
    //endregion
}
