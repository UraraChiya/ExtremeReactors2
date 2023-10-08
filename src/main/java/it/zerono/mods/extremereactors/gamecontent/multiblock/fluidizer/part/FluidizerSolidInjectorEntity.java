/*
 *
 * FluidizerAccessPortEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.container.FluidizerSolidInjectorContainer;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.DebuggableHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.data.nbt.IConditionallySyncableEntity;
import it.zerono.mods.zerocore.lib.data.stack.IStackHolder;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.item.inventory.ItemStackHolder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredientSource;
import it.zerono.mods.zerocore.lib.recipe.ingredient.RecipeIngredientSourceWrapper;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidizerSolidInjectorEntity
        extends AbstractFluidizerEntity
        implements INamedContainerProvider, INeighborChangeListener, IConditionallySyncableEntity {

    public FluidizerSolidInjectorEntity() {

        super(Content.TileEntityTypes.FLUIDIZER_SOLIDINJECTOR.get());
        this._solidItems = new ItemStackHolder(1, ($, stack) -> this.isValidIngredient(stack)).setOnLoadListener(this::onItemsChanged).setOnContentsChangedListener(this::onItemsChanged);
        this._capability = LazyOptional.of(() -> this._solidItems);
    }

    public IRecipeIngredientSource<ItemStack> asRecipeSource() {
        return RecipeIngredientSourceWrapper.wrap(this._solidItems, 0);
    }

    public IItemHandlerModifiable getItemHandler() {
        return this._solidItems;
    }

    public ItemStack getStack() {
        return this._solidItems.getStackAt(0);
    }

    public boolean isValidIngredient(final ItemStack stack) {
        return this.evalOnController(c -> c.isValidIngredient(stack), false);
    }

    public static void itemTooltipBuilder(final ItemStack stack, final CompoundNBT data, final @Nullable IBlockReader world,
                                          final NonNullConsumer<ITextComponent> appender, final boolean isAdvancedTooltip) {

        if (data.contains("inv")) {

            final ItemStackHolder holder = new ItemStackHolder(1);
            IFormattableTextComponent text;

            holder.syncDataFrom(data.getCompound("inv"), SyncReason.FullSync);

            if (holder.isEmpty(0)) {
                text = new TranslationTextComponent("gui.zerocore.base.generic.empty");
            } else {
                text = new TranslationTextComponent("gui.bigreactors.reactor.fluidaccessport.item.reactant",
                        ItemHelper.getItemName(holder.getStackInSlot(0)), holder.getAmount(0));
            }

            appender.accept(new TranslationTextComponent("gui.bigreactors.generic.fuel.label")
                    .append(text.setStyle(CommonConstants.STYLE_TOOLTIP_VALUE)));
        }
    }

    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {
        return this.isMachineAssembled() && this.getNeighborCapability().isPresent() ? 1 : 0;
    }

    //endregion
    //region INeighborChangeListener

    /**
     * Called when a neighboring Block on a side of this TileEntity changes
     *
     * @param state            the BlockState of this TileEntity block
     * @param neighborPosition position of neighbor
     */
    @Override
    public void onNeighborBlockChanged(BlockState state, BlockPos neighborPosition, boolean isMoving) {
        this.requestClientRenderUpdate();
    }

    /**
     * Called when a neighboring TileEntity on a side of this TileEntity changes, is created or is destroyed
     *
     * @param state            the BlockState of this TileEntity block
     * @param neighborPosition position of neighbor
     */
    @Override
    public void onNeighborTileChanged(BlockState state, BlockPos neighborPosition) {
        this.requestClientRenderUpdate();
    }

    //endregion
    //region ISyncableEntity

    @Override
    public void syncDataFrom(CompoundNBT data, SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);
        this.syncChildDataEntityFrom(this._solidItems, "inv", data, syncReason);

        if (syncReason.isFullSync()) {
            this._shouldSync = true;
        }
    }

    @Override
    public CompoundNBT syncDataTo(CompoundNBT data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        this.syncChildDataEntityTo(this._solidItems, "inv", data, syncReason);
        return data;
    }

    //endregion
    //region IConditionallySyncableEntity

    /**
     * @return a unique identifier for this ISyncableEntity
     */
    @Override
    public ResourceLocation getSyncableEntityId() {
        return SYNC_DATA_ID;
    }

    /**
     * @return true if this ISyncableEntity should be synced, false otherwise
     */
    @Override
    public boolean shouldSyncEntity() {

        final boolean result = this._shouldSync;

        this._shouldSync = false;
        return result;
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        super.getDebugMessages(side, messages);
        messages.add(this._solidItems, DebuggableHelper::getDebugMessagesFor, "Items");
    }

    //endregion
    //region INamedContainerProvider

    /**
     * Create the SERVER-side container for this TileEntity
     * @param windowId  the window id
     * @param inventory the player inventory
     * @param player    the player
     * @return the container to use on the server
     */
    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory inventory, final PlayerEntity player) {
        return new FluidizerSolidInjectorContainer(windowId, inventory, this);
    }

    @Override
    public ITextComponent getDisplayName() {
        return super.getPartDisplayName();
    }

    //endregion
    //region AbstractModBlockEntity

    /**
     * Check if the tile entity has a GUI or not
     * Override in derived classes to return true if your tile entity got a GUI
     */
    @Override
    public boolean canOpenGui(World world, BlockPos position, BlockState state) {
        return true;
    }

    //endregion
    //region TileEntity

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return ITEM_HANDLER_CAPABILITY == cap ? this._capability.cast() : super.getCapability(cap, side);
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void setRemoved() {

        super.setRemoved();
        this._capability.invalidate();
    }

    //endregion
    //region internals

    private LazyOptional<IItemHandler> getNeighborCapability() {
        return CodeHelper.optionalFlatMap(this.getPartWorld(), this.getOutwardDirection(),
                        (world, direction) -> WorldHelper.getTile(world, this.getWorldPosition().relative(direction))
                                .map(te -> te.getCapability(ITEM_HANDLER_CAPABILITY, direction.getOpposite())))
                .orElse(LazyOptional.empty());
    }

    private void onItemsChanged(IStackHolder.ChangeType changeType, int slot) {
        this.onItemsChanged();
    }

    private void onItemsChanged() {

        this.setChanged();
        this.onIngredientsChanged();
        this._shouldSync = true;
    }

    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IItemHandler.class)
    private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

    private static final ResourceLocation SYNC_DATA_ID = ExtremeReactors.newID("solidinjector");

    private final ItemStackHolder _solidItems;
    private final LazyOptional<IItemHandlerModifiable> _capability;
    private boolean _shouldSync;

    //endregion
}
