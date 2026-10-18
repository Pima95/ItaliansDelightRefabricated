package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.italiansdelight.common.pizza.PizzaPeelCargo;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

/**
 * Displays the real ItemStack from peel_cargo on the blade, only while held.
 *
 * <p>Crucial: a LOADED peel has the exact same vanilla hand transformations
 * as the approved EMPTY peel. Only its JSON cuboids are rotated by -90 degrees
 * about the handle grip (see pizza_peel_loaded.json). Therefore its grip
 * stays attached to the character's hand in all contexts; there is no second
 * global hand pose to compensate or accidentally apply twice.
 *
 * <p>All geometry is still rendered by Minecraft's normal baked item model.
 * Only the real pizza item receives a small additional overlay layer.
 */
public record PizzaPeelItemModel(ItemModel peelModel) implements ItemModel {
    /*
     * Loaded blade after X=-90 around model grip [8,6.75,8]:
     *   x 4..12, y 6.25..7.25, z -12..1 (in model pixels).
     * Center on top of blade is [8,7.25,-5.5].
     * Offset +0.75 model pixels above the blade gives Y=8 in model space,
     * i.e. Y=0 after conversion to item coordinates (model origin [8,8,8]).
     */
    private static final float CARGO_X = 0.0F;
    private static final float CARGO_Y = 0.0F;
    private static final float CARGO_Z = (-5.5F - 8.0F) / 16.0F;
    private static final float PIZZA_SCALE = 0.40F;
    private static final float PIZZA_DEPTH = 0.055F;
    private static final CargoRenderer CARGO_RENDERER = new CargoRenderer();

    /*
     * These are the ORIGINAL and already approved empty peel display values
     * from models/item/pizza_peel.json. The loaded model inherits them.
     * Minecraft's model display translations are in 1/16 block units.
     */
    private static final ItemTransform THIRD_PERSON =
            transform(0, 60, 0, 0, 3, 0, 0.8F);
    private static final ItemTransform FIRST_PERSON_RIGHT =
            transform(0, -90, 25, 0, 3, 1, 0.7F);
    private static final ItemTransform FIRST_PERSON_LEFT =
            transform(0, 90, -25, 0, 3, 1, 0.7F);

    private static ItemTransform transform(
            float rx, float ry, float rz,
            float tx, float ty, float tz, float scale
    ) {
        return new ItemTransform(
                new Vector3f(rx, ry, rz),
                new Vector3f(tx / 16.0F, ty / 16.0F, tz / 16.0F),
                new Vector3f(scale, scale, scale));
    }

    private static @Nullable ItemTransform handTransform(ItemDisplayContext context) {
        return switch (context) {
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> THIRD_PERSON;
            case FIRST_PERSON_RIGHT_HAND -> FIRST_PERSON_RIGHT;
            case FIRST_PERSON_LEFT_HAND -> FIRST_PERSON_LEFT;
            default -> null;
        };
    }

    @Override
    public void update(
            ItemStackRenderState output, ItemStack peel, ItemModelResolver resolver,
            ItemDisplayContext context, @Nullable ClientLevel level,
            @Nullable ItemOwner owner, int seed
    ) {
        // Minecraft selects baked pizza_peel_loaded cuboids only for a loaded
        // hand-held peel; GUI, hotbar, frames and drops use the approved model.
        // Always use standard item rendering for the peel itself.
        peelModel.update(output, peel, resolver, context, level, owner, seed);

        ItemTransform transform = handTransform(context);
        if (transform == null) {
            return; // no extra pizza image in GUI, hotbar, frames or drops
        }

        PizzaPeelCargo cargo = peel.get(ModDataComponents.PEEL_CARGO);
        if (cargo == null) {
            return;
        }

        ItemStack pizza = cargo.pizza();
        if (pizza.isEmpty() || pizza.is(ModItems.PIZZA_PEEL)) {
            return;
        }

        // Always resolve the *real* carried item so future pizzas with other
        // item models work automatically. NONE prevents the pizza's own hand
        // transformations from being applied on top of the peel transforms.
        TrackingItemStackRenderState child = new TrackingItemStackRenderState();
        resolver.updateForTopItem(
                child, pizza, ItemDisplayContext.NONE, level, owner, seed);
        if (child.isEmpty()) {
            return;
        }

        output.appendModelIdentityElement(this);
        output.appendModelIdentityElement(child.getModelIdentity());

        // Both baked peel and cargo layer use the SAME approved hand pose.
        // Cargo local placement is computed from the rotated blade geometry.
        ItemStackRenderState.LayerRenderState layer = output.newLayer();
        layer.setItemTransform(transform);
        layer.setupSpecialModel(CARGO_RENDERER, child);
    }

    private static final class CargoRenderer
            implements SpecialModelRenderer<ItemStackRenderState> {
        @Override
        public void submit(
                @Nullable ItemStackRenderState state, PoseStack poseStack,
                SubmitNodeCollector queue, int light, int overlay,
                boolean foil, int outline
        ) {
            if (state == null || state.isEmpty()) {
                return;
            }

            poseStack.pushPose();
            poseStack.translate(CARGO_X, CARGO_Y, CARGO_Z);
            // Real pizza item art lies in XY. Turn its front (+Z) upward (+Y)
            // so it rests horizontally on top of the loaded metal paddle.
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.scale(PIZZA_SCALE, PIZZA_SCALE, PIZZA_DEPTH);
            state.submit(poseStack, queue, light, overlay, outline);
            poseStack.popPose();
        }

        @Override
        public void getExtents(Consumer<Vector3fc> consumer) {
            // Bounding cuboid around the pizza on the loaded paddle.
            // Provide all eight vertices to avoid first-person culling.
            for (float x : new float[] {-0.35F, 0.35F}) {
                for (float y : new float[] {-0.35F, 0.35F}) {
                    for (float z : new float[] {-1.15F, -0.55F}) {
                        consumer.accept(new Vector3f(x, y, z));
                    }
                }
            }
        }

        @Override
        public @Nullable ItemStackRenderState extractArgument(ItemStack stack) {
            // update() supplies the resolved render-state directly.
            return null;
        }
    }
}
