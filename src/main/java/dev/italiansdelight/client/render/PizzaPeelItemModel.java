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
 * A loaded peel uses the NORMAL baked Minecraft item model for its 3D mesh.
 * The extra client layer draws ONLY the real carried item on its blade.
 *
 * <p>Do not replace the entire peel model with a special renderer: doing so
 * made it disappear in first person and detached the mesh from the hand.
 * The item JSON handles the loaded-vs-empty baked mesh selection and ALL
 * non-hand contexts always use the approved empty-pose model.
 */
public record PizzaPeelItemModel(ItemModel peelModel) implements ItemModel {
    /*
     * Blade cuboid: from [4,13.75,7.5] to [12,26.75,8.5], relative origin
     * [8,8,8]. The held model's visible steel face is the -Z side;
     * the +Z side put the pizza BEHIND the opaque metal (invisible in
     * first person and visible underneath/through the avatar in third).
     */
    private static final float BLADE_CENTER_Y = (20.25F - 8.0F) / 16.0F;
    private static final float BLADE_FACE_Z = -0.075F;
    private static final float ITEM_SCALE = 0.44F;
    private static final float ITEM_DEPTH = 0.06F;

    /*
     * These transforms MUST mirror the corresponding entries in
     * models/item/pizza_peel_loaded.json. The wooden handle's grip point
     * stays near the player's hand when the plate rotates horizontally.
     * The +Y adjustment fixes the prior lowered grip, while Z=-8px
     * compensates for the 90-degree swing of the shaft around item origin.
     * Minecraft JSON translations are model pixels: divide by 16.
     */
    private static final ItemTransform THIRD_PERSON =
            pose(-90, 0, 0, 0, 3, -8, 0.8F);
    private static final ItemTransform FIRST_PERSON_RIGHT =
            pose(-90, 0, 25, 0, 3, 1, 0.7F);
    private static final ItemTransform FIRST_PERSON_LEFT =
            pose(-90, 0, -25, 0, 3, 1, 0.7F);
    private static final PizzaOnBladeRenderer CARGO_RENDERER = new PizzaOnBladeRenderer();

    private static ItemTransform pose(
            float rx, float ry, float rz,
            float tx, float ty, float tz, float s
    ) {
        return new ItemTransform(
                new Vector3f(rx, ry, rz),
                new Vector3f(tx / 16.0F, ty / 16.0F, tz / 16.0F),
                new Vector3f(s, s, s));
    }

    private static @Nullable ItemTransform loadedTransform(ItemDisplayContext context) {
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
        // Essential: vanilla's baked model handles pose, hands, GUI, lighting,
        // resource packs and bounds. This path is ALWAYS used, including
        // when the peel has a cargo component.
        peelModel.update(output, peel, resolver, context, level, owner, seed);

        ItemTransform heldPose = loadedTransform(context);
        if (heldPose == null) {
            // No pizza over the peel in inventory, hotbar, frame or as drop.
            return;
        }

        PizzaPeelCargo cargo = peel.get(ModDataComponents.PEEL_CARGO);
        if (cargo == null) {
            return;
        }

        ItemStack carried = cargo.pizza();
        if (carried.isEmpty() || carried.is(ModItems.PIZZA_PEEL)) {
            return;
        }

        // This remains the actual ItemStack; new pizza item models are
        // resolved automatically, without a texture/id whitelist.
        TrackingItemStackRenderState itemState = new TrackingItemStackRenderState();
        resolver.updateForTopItem(
                itemState, carried, ItemDisplayContext.NONE, level, owner, seed);
        if (itemState.isEmpty()) {
            return;
        }

        output.appendModelIdentityElement(this);
        output.appendModelIdentityElement(itemState.getModelIdentity());
        ItemStackRenderState.LayerRenderState layer = output.newLayer();
        layer.setItemTransform(heldPose);
        layer.setupSpecialModel(CARGO_RENDERER, itemState);
    }

    /**
     * One optional pizza layer. It never draws or transforms the peel mesh;
     * the latter is drawn by Minecraft's standard baked model above.
     */
    private static final class PizzaOnBladeRenderer
            implements SpecialModelRenderer<ItemStackRenderState> {
        @Override
        public void submit(
                @Nullable ItemStackRenderState contents, PoseStack poses,
                SubmitNodeCollector queue, int light, int overlay,
                boolean foil, int outline
        ) {
            if (contents == null || contents.isEmpty()) {
                return;
            }

            poses.pushPose();
            // Draw on the -Z face of the metal blade, not behind the
            // opaque +Z face. Do not rotate the approved peel model.
            poses.translate(0.0F, BLADE_CENTER_Y, BLADE_FACE_Z);
            poses.scale(ITEM_SCALE, ITEM_SCALE, ITEM_DEPTH);
            contents.submit(poses, queue, light, overlay, outline);
            poses.popPose();
        }

        @Override
        public void getExtents(Consumer<Vector3fc> consumer) {
            // All eight corners, not two diagonal points. This covers the
            // plate and the emblem after the loaded X=-90 degree rotation.
            for (float x : new float[] {-0.65F, 0.65F}) {
                for (float y : new float[] {-0.75F, 1.75F}) {
                    for (float z : new float[] {-0.65F, 0.65F}) {
                        consumer.accept(new Vector3f(x, y, z));
                    }
                }
            }
        }

        @Override
        public @Nullable ItemStackRenderState extractArgument(ItemStack stack) {
            // The actual render-state payload is supplied by update().
            return null;
        }
    }
}
