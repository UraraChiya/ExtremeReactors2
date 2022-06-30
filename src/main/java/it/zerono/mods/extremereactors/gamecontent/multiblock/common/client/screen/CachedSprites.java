/*
 *
 * CachedSprites.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.zerocore.lib.client.gui.sprite.AtlasSpriteSupplier;
import it.zerono.mods.zerocore.lib.client.gui.sprite.AtlasSpriteTextureMap;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class CachedSprites {

    public static final Supplier<ISprite> REACTOR_FUEL_COLUMN_STILL;
    public static final Supplier<ISprite> REACTOR_FUEL_COLUMN_FLOWING;
    public static final Supplier<ISprite> WATER_SOURCE;

    public static final ResourceLocation GUI_CHARGINGPORT_SLOT_ID;
    public static final Supplier<ISprite> GUI_CHARGINGPORT_SLOT;

    public static final Supplier<ISprite> VANILLA_BUCKET;

    public static void initialize() {
    }

    static {

        REACTOR_FUEL_COLUMN_STILL = AtlasSpriteSupplier.create(ExtremeReactors.newID("fluid/fluid.fuelcolumn.still"), AtlasSpriteTextureMap.BLOCKS, true);
        REACTOR_FUEL_COLUMN_FLOWING = AtlasSpriteSupplier.create(ExtremeReactors.newID("fluid/fluid.fuelcolumn.flowing"), AtlasSpriteTextureMap.BLOCKS, true);
        WATER_SOURCE = AtlasSpriteSupplier.create(CommonConstants.FLUID_TEXTURE_SOURCE_WATER, AtlasSpriteTextureMap.BLOCKS, true);

        GUI_CHARGINGPORT_SLOT_ID = ExtremeReactors.newID("gui/multiblock/charging");
        GUI_CHARGINGPORT_SLOT = AtlasSpriteSupplier.create(GUI_CHARGINGPORT_SLOT_ID, AtlasSpriteTextureMap.BLOCKS);

        VANILLA_BUCKET  = AtlasSpriteSupplier.create(new ResourceLocation("minecraft:item/bucket"), AtlasSpriteTextureMap.BLOCKS, false);
    }

    //region internals

    private CachedSprites() {
    }

    //endregion
}
