/*
 *
 * BladeSpan.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.render.rotor;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorBladeState;
import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3fc;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class BladeSpan
        implements Consumer<@NotNull PoseStack> {

    public final RotorBladeState State;
    public final short Length;
    public final Direction Direction;
    public final ModelData BladeModelData;
    public final Vector3fc Translation;

    public static BladeSpan from(final RotorBladeState state, final short length, final Direction direction) {
        return s_cache.computeIfAbsent(key(state, length, direction), k -> new BladeSpan(state, length, direction));
    }

    public static int key(final RotorBladeState state, final short length, final Direction direction) {

        final byte stateValue = (byte)(state.ordinal());
        final byte directionValue = (byte)(direction.ordinal());

        return (stateValue << 24) | (directionValue << 16) | length;
    }

    //region NonNullConsumer<PoseStack>

    @Override
    public void accept(final PoseStack stack) {
        stack.last().pose().translate(this.Translation);
    }

    //endregion
    //region Object

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final BladeSpan bladeSpan = (BladeSpan)o;

        return this.Length == bladeSpan.Length &&
                this.State == bladeSpan.State &&
                this.Direction == bladeSpan.Direction;
    }

    @Override
    public int hashCode() {
        return key(this.State, this.Length, this.Direction);
    }

    //endregion
    //region internals

    private BladeSpan(final RotorBladeState state, final short length, final Direction direction) {

        this.State = state;
        this.Length = length;
        this.Direction = direction;
        this.BladeModelData = ComponentModelData.from(state);
        this.Translation = direction.step();
    }

    private static final Int2ObjectMap<BladeSpan> s_cache = new Int2ObjectOpenHashMap<>(8);

    //endregion
}
