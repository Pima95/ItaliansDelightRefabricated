package dev.italiansdelight.client.render;

import dev.italiansdelight.common.pizza.PizzaPeelCargo;
import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Draw the APPROVED 3D peel as normal, and add the actual carried item model.
 *
 * <p>Uses the persisted ItemStack inside peel_cargo, NOT a hard-coded pizza
 * item ID, flavour name, or snapshot texture. Future pizza item types,
 * resource packs and their item models therefore work without modifying
 * this renderer. Model loading wraps the already baked peel model so that
 * all its approved UVs, geometry and hand transformations stay intact.
 *
 * <p>Client-side model extraction only. No special renderer or world-state
 * lookup is required, and no additional pizza item is created on the server.
 */
public record PizzaPeelItemModel(ItemModel peelModel) implements ItemModel {
    @Override
    public void update(
            ItemStackRenderState output, ItemStack peel, ItemModelResolver resolver,
            ItemDisplayContext context, @Nullable ClientLevel level,
            @Nullable ItemOwner owner, int seed
    ) {
        peelModel.update(output, peel, resolver, context, level, owner, seed);

        PizzaPeelCargo cargo = peel.get(ModDataComponents.PEEL_CARGO);
        if (cargo == null) {
            return;
        }

        ItemStack carried = cargo.pizza();
        if (carried.isEmpty() || carried.is(ModItems.PIZZA_PEEL)) {
            // Malformed/old component must not recurse endlessly.
            return;
        }

        // This is the *real* item-model resolver, with the real ItemStack.
        // It honors ITEM_MODEL components, future pizza item definitions,
        // resource-pack models and all carried item data.
        resolver.appendItemLayers(output, carried, context, level, owner, seed);
    }
}
