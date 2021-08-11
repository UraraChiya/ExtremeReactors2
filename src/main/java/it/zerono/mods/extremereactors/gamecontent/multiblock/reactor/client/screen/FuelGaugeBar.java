/*
 *
 * FuelGaugeBar.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.zerono.mods.extremereactors.api.reactor.ReactantType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CachedSprites;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.control.MultiValueGaugeBar;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.List;

public class FuelGaugeBar
        extends MultiValueGaugeBar<ReactantType> {

    public FuelGaugeBar(ModContainerScreen<? extends ModContainer> gui, String name, MultiblockReactor reactor) {

        super(gui, name, reactor.getCapacity(), ReactantType.Waste, ReactantType.Fuel);
        this.setBarSprite(ReactantType.Fuel, CachedSprites.REACTOR_FUEL_COLUMN_FLOWING);
        this.setBarSprite(ReactantType.Waste, CachedSprites.REACTOR_FUEL_COLUMN_FLOWING);
        this._reactor = reactor;

        this._tooltipsLines = ImmutableList.of(
                new TranslatableComponent("gui.bigreactors.reactor.controller.fuelbar.line1").setStyle(AbstractMultiblockScreen.STYLE_TOOLTIP_TITLE),
                new TranslatableComponent("gui.bigreactors.reactor.controller.fuelbar.line2"),
                new TranslatableComponent("gui.bigreactors.reactor.controller.fuelbar.line3"),
                CodeHelper.TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.reactor.controller.fuelbar.line4", String.format(ChatFormatting.DARK_AQUA + "" + ChatFormatting.BOLD + "%d", this._reactor.getFuelRodsCount())),
                new TranslatableComponent("gui.bigreactors.reactor.controller.fuelbar.line5", ChatFormatting.DARK_AQUA + "" + ChatFormatting.BOLD + CodeHelper.formatAsMillibuckets(this._reactor.getCapacity())),
                new TranslatableComponent("gui.bigreactors.reactor.controller.fuelbar.line6"),
                new TranslatableComponent("gui.bigreactors.reactor.controller.fuelbar.line7"),
                new TranslatableComponent("gui.bigreactors.reactor.controller.fuelbar.line8")
        );

        this._tooltipsObjects = ImmutableList.of(
                // @0
                (NonNullSupplier<Component>)() -> new TextComponent(String.format("%.2f%%", this.getFillRatio() * 100f))
                        .setStyle(AbstractMultiblockScreen.STYLE_TOOLTIP_VALUE),
                // @1
                (NonNullSupplier<Component>)() -> new TextComponent(String.format("%.2f%%", this._reactor.getFuelContainer().isEmpty() ?
                        0f : ((float)this._reactor.getFuelContainer().getWasteAmount() / (float)(this.getFuelAmount() + this.getWasteAmount())) * 100f))
                            .setStyle(AbstractMultiblockScreen.STYLE_TOOLTIP_VALUE),
                // @2
                (NonNullSupplier<Component>)() -> (this.getFuelAmount() > 0 ?
                        new TextComponent(CodeHelper.formatAsMillibuckets(this.getFuelAmount())) : EMPTY)
                            .setStyle(AbstractMultiblockScreen.STYLE_TOOLTIP_VALUE),
                // @3
                (NonNullSupplier<Component>)() -> (this.getWasteAmount() > 0 ?
                        new TextComponent(CodeHelper.formatAsMillibuckets(this.getWasteAmount())) : EMPTY)
                        .setStyle(AbstractMultiblockScreen.STYLE_TOOLTIP_VALUE),
                // @4
                (NonNullSupplier<Component>)() -> new TextComponent(CodeHelper.formatAsMillibuckets(this.getFuelAmount() + this.getWasteAmount()))
                        .setStyle(AbstractMultiblockScreen.STYLE_TOOLTIP_VALUE)
        );
    }

    //region IControl

    @Override
    public List<Component> getTooltips() {
        return this._tooltipsLines;
    }

    @Override
    public List<Object> getTooltipsObjects() {
        return this._tooltipsObjects;
    }

    //endregion
    //region MultiValueGaugeBar

    @Override
    protected int paintValueRect(final PoseStack matrix, final ReactantType reactantType, final Rectangle rect, final int skip) {

        RenderSystem.enableBlend();
        int r = super.paintValueRect(matrix, reactantType, rect, skip);
        RenderSystem.disableBlend();
        return r;
    }

    //endregion
    //region internals

    private int getFuelAmount() {
        return this._reactor.getFuelContainer().getFuelAmount();
    }

    private int getWasteAmount() {
        return this._reactor.getFuelContainer().getWasteAmount();
    }

    private static final MutableComponent EMPTY = new TranslatableComponent("gui.bigreactors.generic.empty");

    private final MultiblockReactor _reactor;
    private final List<Component> _tooltipsLines;
    private final List<Object> _tooltipsObjects;

    //endregion
}
