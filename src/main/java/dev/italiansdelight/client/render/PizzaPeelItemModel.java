package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
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
 * Render the approved peel unmodified when empty or outside the player's
 * hands. When loaded AND held, render the peel and the *real* carried item
 * under ONE shared transform, so the pizza cannot float by the player's
 * torso when the peel's orientation changes.
 *
 * <p>The common mesh is NEVER replaced: this wrapper uses the original baked
 * model. Inventory / hotbar / ground / frame never show the cargo overlay.
 * No pizza item ID, ingredient or resource-pack model is hard-coded.
 */
public record PizzaPeelItemModel(ItemModel peelModel) implements ItemModel {
    /*
     * Approved mesh in models/item/pizza_peel.json:
     * metal face x 4..12, y 13.75..26.75, z 7.5..8.5 (model pixels).
     * Center above face relative to [8,8,8]:
     * X = 0, Y = (20.25-8)/16 = .765625, Z = .5/16 + clearance.
     */
    private static final float PIZZA_CENTER_Y = 0.765625F;
    private static final float PIZZA_SURFACE_Z = 0.0725F;
    private static final float PIZZA_SIZE = 0.34F;
    private static final float PIZZA_DEPTH = 0.06F;

    /*
     * Rotate +Z, the front of the metal face, to +Y (up): X -90 degrees.
     * THIRD PERSON: moving the grip end to the old empty-peel grip requires
     * compensating its old Y=-8px pivot; the horizontal handle also needs
     * Z=-8px so the hand surrounds the wooden end, not the middle of the blade.
     *
     * For first person retain the already-approved loaded-peel position.
     * JSON transforms are in sixteenths; ItemTransform uses block units.
     */
    private static final ItemTransform THIRD_PERSON =
            transform(-90, 0, 0, 0, -5, -8, 0.8F);
    private static final ItemTransform FIRST_PERSON_RIGHT =
            transform(-90, 0, 25, 0, 3, 1, 0.7F);
    private static final ItemTransform FIRST_PERSON_LEFT =
            transform(-90, 0, -25, 0, 3, 1, 0.7F);

    private static final LoadedPeelRenderer LOADED_RENDERER = new LoadedPeelRenderer();

    private static ItemTransform transform(
            float rx, float ry, float rz,
            float tx, float ty, float tz, float size
    ) {
        return new ItemTransform(
                new Vector3f(rx, ry, rz),
                new Vector3f(tx / 16.0F, ty / 16.0F, tz / 16.0F),
                new Vector3f(size, size, size));
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
        ItemTransform heldPose = handTransform(context);
        PizzaPeelCargo cargo = peel.get(ModDataComponents.PEEL_CARGO);

        // Keep the approved empty pose. Outside the hands this branch also
        // prevents cargo rendering in GUI/hotbar, item frames and on ground.
        if (heldPose == null || cargo == null) {
            peelModel.update(output, peel, resolver, context, level, owner, seed);
            return;
        }

        ItemStack carried = cargo.pizza();
        if (carried.isEmpty() || carried.is(ModItems.PIZZA_PEEL)) {
            peelModel.update(output, peel, resolver, context, level, owner, seed);
            return;
        }

        // Do NOT render the peel as an independent output layer. The former
        // implementation mixed its baked pose with a separate cargo pose,
        // causing the blade and pizza to end up at different world positions.
        // Resolve both models in neutral NONE space, then draw both with the
        // SAME heldPose and the same PoseStack in LoadedPeelRenderer.submit().
        TrackingItemStackRenderState peelState = new TrackingItemStackRenderState();
        peelModel.update(peelState, peel, resolver, ItemDisplayContext.NONE, level, owner, seed);

        TrackingItemStackRenderState pizzaState = new TrackingItemStackRenderState();
        resolver.updateForTopItem(
                pizzaState, carried, ItemDisplayContext.NONE, level, owner, seed);
        if (peelState.isEmpty()) {
            return;
        }

        output.appendModelIdentityElement(this);
        output.appendModelIdentityElement(peelState.getModelIdentity());
        if (!pizzaState.isEmpty()) {
            output.appendModelIdentityElement(pizzaState.getModelIdentity());
        }

        ItemStackRenderState.LayerRenderState layer = output.newLayer();
        layer.setItemTransform(heldPose);
        layer.setupSpecialModel(LOADED_RENDERER, new HeldContents(peelState, pizzaState));
    }

    private record HeldContents(
            ItemStackRenderState peel,
            ItemStackRenderState pizza
    ) {
    }

    private static final class LoadedPeelRenderer
            implements SpecialModelRenderer<HeldContents> {
        @Override
        public void submit(
                @Nullable HeldContents contents, PoseStack poseStack,
                SubmitNodeCollector sink, int light, int overlay,
                boolean foil, int outlineColour
        ) {
            if (contents == null) {
                return;
            }

            // First the approved metal/wood 3D geometry in neutral item space.
            contents.peel().submit(poseStack, sink, light, overlay, outlineColour);

            // Then the real pizza item, ON the front (+Z) of the metal blade.
            // The common outer transform rotates both toward world +Y,
            // making the pizza face upward and the peel parallel to ground.
            if (!contents.pizza().isEmpty()) {
                poseStack.pushPose();
                poseStack.translate(0.0F, PIZZA_CENTER_Y, PIZZA_SURFACE_Z);
                poseStack.scale(PIZZA_SIZE, PIZZA_SIZE, PIZZA_DEPTH);
                contents.pizza().submit(
                        poseStack, sink, light, overlay, outlineColour);
                poseStack.popPose();
            }
        }

        @Override
        public void getExtents(Consumer<Vector3fc> consumer) {
            // Full head, grip and cargo bounds. An empty extent previously
            // allowed culling to hide the pizza in first person.
            consumer.accept(new Vector3f(-0.3F, -0.5F, -0.15F));
            consumer.accept(new Vector3f(0.3F, 1.18F, 0.15F));
        }

        @Override
        public @Nullable HeldContents extractArgument(ItemStack stack) {
            // The immutable render-state argument is supplied by update().
            return null;
        }
    }
}
