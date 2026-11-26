package dev.italiansdelight.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.italiansdelight.common.pizza.PizzaBaking;
import dev.italiansdelight.common.pizza.PizzaFurnaceRemainders;
import dev.italiansdelight.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** NeoForge 1.21.1 assembles the actual input in canBurn and burn.
 * Only deliver returned containers after burn reports success.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class PizzaVanillaFurnaceMixin {
    @WrapOperation(method = "serverTick", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;burn(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/core/NonNullList;ILnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)Z"))
    private static boolean italiansdelight$preservePizzaOnBurn(
            RegistryAccess registries, RecipeHolder<?> recipe, NonNullList<ItemStack> items,
            int maxStackSize, AbstractFurnaceBlockEntity furnace, Operation<Boolean> original,
            @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        ItemStack input = items.get(0);
        var baked = input.is(ModItems.RAW_PIZZA)
            ? PizzaBaking.finish(input, PizzaBaking.MAX_PROGRESS)
            : java.util.Optional.<PizzaBaking.BakingResult>empty();
        boolean success = original.call(registries, recipe, items, maxStackSize, furnace);
        if (success && level instanceof ServerLevel server && items.get(2).is(ModItems.COOKED_PIZZA) && baked.isPresent()) {
            for (ItemStack containers : baked.get().containers()) {
                PizzaFurnaceRemainders.deliver(server, pos, containers);
            }
        }
        return success;
    }
}
