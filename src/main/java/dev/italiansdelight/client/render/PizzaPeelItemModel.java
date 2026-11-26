package dev.italiansdelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

/** Preserve the approved empty and loaded geometry and render actual cargo in hand only. */
public final class PizzaPeelItemModel extends BakedModelWrapper<BakedModel> {
    public static final ModelResourceLocation BASE = ModelResourceLocation.inventory(ModRegistries.id("pizza_peel"));
    public static final ModelResourceLocation LOADED = ModelResourceLocation.standalone(ModRegistries.id("item/pizza_peel_loaded"));
    public PizzaPeelItemModel(BakedModel original) { super(original); }

    @Override public boolean isCustomRenderer() { return true; }
    @Override public BakedModel applyTransform(ItemDisplayContext context, PoseStack stack, boolean left) {
        // The custom renderer applies the appropriate baked model's transform once.
        return this;
    }

    public static final class Renderer extends BlockEntityWithoutLevelRenderer {
        public Renderer() {
            super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }

        @Override public void renderByItem(ItemStack peel, ItemDisplayContext context, PoseStack stack,
                MultiBufferSource buffers, int light, int overlay) {
            var mc = Minecraft.getInstance();
            var wrapper = mc.getModelManager().getModel(BASE);
            if (!(wrapper instanceof PizzaPeelItemModel model)) return;
            var cargo = peel.get(ModDataComponents.PEEL_CARGO);
            boolean hand = switch (context) {
                case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> true;
                default -> false;
            };
            boolean left = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            boolean loaded = hand && cargo != null && !cargo.pizza().isEmpty();
            BakedModel geometry = loaded ? mc.getModelManager().getModel(LOADED) : model.originalModel;
            stack.pushPose();
            // Cancel ItemRenderer's outer centering before rendering the inner baked model.
            stack.translate(0.5F, 0.5F, 0.5F);
            mc.getItemRenderer().render(peel, context, left, stack, buffers, light, overlay, geometry);
            if (loaded) {
                geometry.getTransforms().getTransform(context).apply(left, stack);
                stack.translate(0, 7.75F/16F - 0.5F, -5.5F/16F - 0.5F);
                stack.mulPose(Axis.XP.rotationDegrees(-90));
                stack.scale(0.40F, 0.40F, 0.055F);
                mc.getItemRenderer().renderStatic(cargo.pizza(), ItemDisplayContext.NONE, light, overlay,
                    stack, buffers, mc.level, 0);
            }
            stack.popPose();
        }
    }
}
