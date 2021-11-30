/*
 *
 * ReactorChargingPort.java
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

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.ChargingPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap.chargingport.AbstractChargingPortHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap.chargingport.IChargingPort;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap.chargingport.IChargingPortHandler;
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ReactorChargingPortEntity
        extends AbstractReactorPowerTapEntity
        implements IChargingPort, MenuProvider {

    public ReactorChargingPortEntity(final EnergySystem system, final BlockEntityType<?> entityType,
                                     final BlockPos position, final BlockState blockState) {

        super(system, entityType, position, blockState);
        this.setHandler(IChargingPortHandler.create(system, this));

        this.setCommandDispatcher(TileCommandDispatcher.<ReactorChargingPortEntity>builder()
                .addServerHandler(AbstractChargingPortHandler.TILE_COMMAND_EJECT, tile -> tile.getChargingPortHandler().eject())
                .build(this));
    }

    //region IChargingPort

    @Override
    public IChargingPortHandler getChargingPortHandler() {
        return (IChargingPortHandler)this.getPowerTapHandler();
    }

    //endregion
    //region ISyncableEntity

    @Override
    public void syncDataFrom(CompoundTag data, SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);
        this.getChargingPortHandler().syncDataFrom(data, syncReason);
    }

    @Override
    public CompoundTag syncDataTo(CompoundTag data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        this.getChargingPortHandler().syncDataTo(data, syncReason);
        return data;
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
        return new ChargingPortContainer<>(windowId, Content.ContainerTypes.REACTOR_CHARGINGPORT.get(), inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return super.getPartDisplayName();
    }

    //endregion
    //region AbstractModBlockEntity

    /**
     * Check if the tile entity has a GUI or not
     * Override in derived classes to return true if your tile entity got a GUI
     *
     * @param world
     * @param position
     * @param state
     */
    @Override
    public boolean canOpenGui(Level world, BlockPos position, BlockState state) {
        return true;
    }

    //endregion
}
