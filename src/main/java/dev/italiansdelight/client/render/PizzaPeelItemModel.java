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
 * A loaded peel displays the REAL carried ItemStack on its metal blade, but
 * only while held in first or third person.
 *
 * <p>Never append cargo layers in GUI, hotbar, ground or item frame contexts.
 * The approved peel model and its UVs are left entirely unchanged.
 * Future pizza item types are rendered by the usual Minecraft item model
 * resolver, so they need no hard-coded special cases here.
 */
public record PizzaPeelItemModel(ItemModel peelModel) implements ItemModel {
    /*
     * The approved peel model has a metal blade from [4,13.75,7.5] to
     * [12,26.75,8.5] in 0..16 cuboid coordinates. Item models are centered
     * around [8,8,8], so the middle of its visible face is:
     *
     * x = (8 - 8) / 16 = 0
     * y = (20.25 - 8) / 16 = 0.765625
     * z = (8.5 - 8) / 16 = 0.03125, plus a small offset for visibility.
     *
     * An item-model size of 0.34 covers about 5.4 of the blade's 8px width.
     * The thin depth keeps the pizza emblem close to the steel surface.
     */
    private static final float BLADE_Y = 0.765625F;
    private static final float BLADE_Z = 0.070F;
    private static final float PIZZA_SCALE = 0.34F;
    private static final float PIZZA_THICKNESS = 0.04F;
    private static final BladeCargoRenderer CARGO_RENDERER = new BladeCargoRenderer();

    /*
     * The extra render-state layer needs the SAME display transformation as
     * the approved peel's baked JSON. Otherwise it is rendered around the
     * player's hand/origin instead of following the blade.
     *
     * JSON translations are in 1/16 units; ItemTransform takes block units.
     * The three values are copied from models/item/pizza_peel.json and must
     * be kept in sync if that approved model is changed in the future.
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
            case FIRST_PERSON_RIGHT_HAND -> FIRST_PERSON_RIGHT;
            case FIRST_PERSON_LEFT_HAND -> FIRST_PERSON_LEFT;
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> THIRD_PERSON;
            default -> null;
        };
    }

    @Override
    public void update(
            ItemStackRenderState output, ItemStack peel, ItemModelResolver resolver,
            ItemDisplayContext context, @Nullable ClientLevel level,
            @Nullable ItemOwner owner, int seed
    ) {
        // Always draw the approved 3D peel first, in every display context.
        peelModel.update(output, peel, resolver, context, level, owner, seed);

        ItemTransform hand = handTransform(context);
        if (hand == null) {
            // GUI includes both inventory and hotbar. Never show pizza there.
            return;
        }

        PizzaPeelCargo cargo = peel.get(ModDataComponents.PEEL_CARGO);
        if (cargo == null) {
            return;
        }

        ItemStack carried = cargo.pizza();
        if (carried.isEmpty() || carried.is(ModItems.PIZZA_PEEL)) {
            // Do not recurse on malformed cargo.
            return;
        }

        // Resolve the ACTUAL item, with its own ITEM_MODEL/data components.
        // NONE avoids inheriting unrelated item-in-hand display rotations.
        // Tracking state keeps icon/model caching coherent on stack changes.
        TrackingItemStackRenderState child = new TrackingItemStackRenderState();
        resolver.updateForTopItem(
                child, carried, ItemDisplayContext.NONE, level, owner, seed);
        if (child.isEmpty()) {
            return;
        }

        output.appendModelIdentityElement(this);
        output.appendModelIdentityElement(child.getModelIdentity());

        ItemStackRenderState.LayerRenderState layer = output.newLayer();
        layer.setItemTransform(hand);
        layer.setupSpecialModel(CARGO_RENDERER, child);
    }

    /**
     * An independent render-state layer lets us position the actual pizza on
     * the peel blade without modifying the approved mesh or baking new models.
     */
    private static final class BladeCargoRenderer
            implements SpecialModelRenderer<ItemStackRenderState> {
        @Override
        public void submit(
                @Nullable ItemStackRenderState state, PoseStack poseStack,
                SubmitNodeCollector sink, int light, int overlay,
                boolean foil, int outlineColour
        ) {
            if (state == null || state.isEmpty()) {
                return;
            }

            poseStack.pushPose();
            poseStack.translate(0.0F, BLADE_Y, BLADE_Z);
            poseStack.scale(PIZZA_SCALE, PIZZA_SCALE, PIZZA_THICKNESS);
            state.submit(poseStack, sink, light, overlay, outlineColour);
            poseStack.popPose();
        }

        @Override
        public void getExtents(Consumer<Vector3fc> consumer) {
            // The extra pizza must not expand the peel's GUI bounding box.
        }

        @Override
        public @Nullable ItemStackRenderState extractArgument(ItemStack stack) {
            // State is created in update() and supplied to setupSpecialModel.
            return null;
        }
    }
}
