/*
 *
 * ReprocessorAccessPortScreen.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.client.screen;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.MultiblockReprocessor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.container.ReprocessorAccessPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part.ReprocessorAccessPortEntity;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.SlotsGroup;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.NonNullSupplier;

public class ReprocessorAccessPortScreen
        extends CommonMultiblockScreen<MultiblockReprocessor, ReprocessorAccessPortEntity, ReprocessorAccessPortContainer> {

    public ReprocessorAccessPortScreen(final ReprocessorAccessPortContainer container,
                                       final PlayerInventory inventory, final ITextComponent title) {
        super(container, inventory, PlayerInventoryUsage.Both, title,
                () -> new SpriteTextureMap(ExtremeReactors.newID("textures/gui/multiblock/basic_background.png"), 256, 256));

        final boolean inletMode = container.getDirection().isInput();

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, ExtremeReactors.newID(inletMode ? "reprocessor/part-wasteinjector" : "reprocessor/part-outputport"), 1);

        this._slotGroup = this.slotPanel("slot", "inv", 0, 0, inletMode ? CommonIcons.PortInputSlot : CommonIcons.PortOutputSlot);
        this._playerInventoryGroup = this.createPlayerInventorySlotsGroupControl();
        this._playerHotBarGroup = this.createPlayerHotBarSlotsGroupControl();
    }

    //region AbstractMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(ReprocessorAccessPortContainer container) {
        return this.createReprocessorStatusIndicator(container.ACTIVE);
    }

    /**
     * Called when this screen is being created for the first time.
     * Override to handle this event
     */
    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();

        final Panel panel = new Panel(this, "accessport");

        panel.setLayoutEngineHint(FixedLayoutEngine.hint(0, 13));
        panel.setDesiredDimension(this.getGuiWidth(), 38);
        panel.setLayoutEngine(new VerticalLayoutEngine()
                .setHorizontalAlignment(HorizontalAlignment.Center)
                .setZeroMargins());
        panel.addControl(this._slotGroup);
        this.addControl(panel);

        this._playerInventoryGroup.setLayoutEngineHint(FixedLayoutEngine.hint(31, 63));
        this.addControl(this._playerInventoryGroup);

        this._playerHotBarGroup.setLayoutEngineHint(FixedLayoutEngine.hint(31, 121));
        this.addControl(this._playerHotBarGroup);
    }

    //endregion
    //region  internals

    private Panel slotPanel(final String groupName, final String invName, final int x, final int y,
                            final NonNullSupplier<ISprite> slotBackground) {

        final SlotsGroup sg = this.createSingleSlotGroupControl(groupName, invName);
        final Panel p = new Panel(this);

        sg.setLayoutEngineHint(FixedLayoutEngine.hint(10, 10, 18, 18));

        p.setBackground(slotBackground.get());
        p.setDesiredDimension(38, 38);
        p.setLayoutEngine(new FixedLayoutEngine().setZeroMargins());
        p.addControl(sg);

        return p;
    }

    //endregion
    //region internals

    private final IControl _slotGroup;
    private final IControl _playerInventoryGroup;
    private final IControl _playerHotBarGroup;

    //endregion
}
