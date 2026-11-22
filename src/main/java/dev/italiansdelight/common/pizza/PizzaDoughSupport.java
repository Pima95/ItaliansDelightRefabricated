package dev.italiansdelight.common.pizza;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

/**
 * Use exactly the same upper-surface test as Farmer's Delight Refabricated
 * placeable Feasts (FeastBlock.canSurvive in Minecraft 26.3).
 *
 * This shared rule covers wheat dough at every stretching stage, formed raw
 * pizzas, baked whole pizzas, and individual cooked slices. In particular,
 * do not maintain an independent whitelist of vanilla blocks: it would
 * reject tables/cabinets and modded supports accepted by Farmer's Delight.
 *
 * The blocks' own canSurvive methods call this both on placement and after
 * neighbor changes; support loss still destroys the pizza through vanilla
 * drops rather than discarding its saved toppings/servings.
 */
public final class PizzaDoughSupport {
    private PizzaDoughSupport() {}

    public static boolean canSupport(BlockGetter level, BlockPos supportPos) {
        return Block.canSupportRigidBlock(level, supportPos);
    }
}
