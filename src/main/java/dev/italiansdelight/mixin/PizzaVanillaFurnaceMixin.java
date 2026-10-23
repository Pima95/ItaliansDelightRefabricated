package dev.italiansdelight.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.italiansdelight.common.pizza.PizzaBaking;
import dev.italiansdelight.common.pizza.PizzaFurnaceRemainders;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Keep the standard vanilla smelting/smoking mechanics (fuel, clock, XP,
 * menu and hoppers), but complete pizza transactions with all original
 * data and the same returned-bowl policy as the dedicated Pizza Oven.
 *
 * <p>Hook only the ACTUAL successful burn call; not the recipe lookup or
 * the cook-time update. This prevents remainders on aborted/blocked cooks.
 * The normal canBurn check has already compared the fully data-aware recipe
 * result with the output ItemStack and verified there is space.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class PizzaVanillaFurnaceMixin {
    @WrapOperation(
            method = "serverTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;burn(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V"
            )
    )
    private static void italiansdelight$preservePizzaOnBurn(
            NonNullList<ItemStack> items,
            ItemStack input,
            ItemStack result,
            Operation<Void> original,
            @Local(argsOnly = true) ServerLevel level,
            @Local(argsOnly = true) BlockPos pos
    ) {
        if (!input.is(ModItems.RAW_PIZZA) || !result.is(ModItems.COOKED_PIZZA)) {
            original.call(items, input, result);
            return;
        }

        var baked = PizzaBaking.finish(input, PizzaBaking.MAX_PROGRESS);
        if (baked.isEmpty()) {
            // Defensive fallback; should be unreachable because a pizza
            // recipe with invalid component data returns an empty output.
            original.call(items, input, result);
            return;
        }

        PizzaBaking.BakingResult pizza = baked.get();
        // Vanilla burn consumes the raw input and commits exactly one output.
        original.call(items, input, pizza.cookedPizza());

        // This runs only AFTER the commit. Never produce a bowl on failed,
        // paused or canceled smelting. Counts derive from original
        // applications, not quarter count or unique sauce categories.
        for (ItemStack containers : pizza.containers()) {
            PizzaFurnaceRemainders.deliver(level, pos, containers);
        }
    }
}
