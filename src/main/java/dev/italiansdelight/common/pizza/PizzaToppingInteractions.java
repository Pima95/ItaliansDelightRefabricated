package dev.italiansdelight.common.pizza;

import dev.italiansdelight.common.block.PizzaDoughBlock;
import dev.italiansdelight.common.block.entity.PizzaDoughBlockEntity;
import dev.italiansdelight.common.registry.ModBlocks;
import dev.italiansdelight.common.registry.ModItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Authoritative interactions on the four physical pizza quarters.
 *
 * <p>No client-side mutation. The composition is replaced only after all
 * canonical and per-quarter validations have passed. As long as the block is
 * alive its entity owns the original stack, so a later break can transfer all
 * topping data into the existing raw_pizza item component.
 *
 * <p>Bowl/container toppings (tomato sauce, cream bowl, ricotta) are held back
 * until the container/refund accounting is implemented without a duplication
 * exploit. SOLID toppings are usable immediately. Rendering is a separate
 * client synchronization milestone.
 */
public final class PizzaToppingInteractions {
    private PizzaToppingInteractions() {
    }

    public static boolean isImplementedTopping(ItemStack held) {
        return PizzaToppingMappings.find(held)
                .filter(type -> type.role() == PizzaToppingType.Role.SOLID)
                .isPresent();
    }

    /** Right-click with one approved, prepared, SOLID ingredient. */
    public static InteractionResult add(
            Player player, Level level, BlockPos pos, BlockHitResult hit, ItemStack held
    ) {
        if (!player.mayUseItemAt(pos, hit.getDirection(), held)) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel server)
                || !server.getBlockState(pos).is(ModBlocks.PIZZA_DOUGH)
                || server.getBlockState(pos).getValue(PizzaDoughBlock.STAGE)
                        != PizzaDoughBlock.MAX_STAGE
                || !(server.getBlockEntity(pos) instanceof PizzaDoughBlockEntity pizza)) {
            return InteractionResult.FAIL;
        }
        // Repeat the lookup on the server: never trust a client-side preview
        // or allow a generic item to be stored as another canonical topping.
        if (!isImplementedTopping(held)) {
            return InteractionResult.PASS;
        }

        PizzaComposition current = pizza.composition();
        int zone = PizzaQuarterSelection.selectMask(current, pos, hit.getLocation());
        Optional<PizzaComposition> updated =
                PizzaCompositionOperations.add(current, held, zone);
        if (updated.isEmpty()) {
            return InteractionResult.SUCCESS; // No consumption on rejection.
        }

        // One item per successful application, regardless of physical area.
        pizza.setComposition(updated.get());
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        server.sendBlockUpdated(pos, server.getBlockState(pos),
                server.getBlockState(pos), Block.UPDATE_CLIENTS);
        server.playSound(null, pos, SoundEvents.WOOL_PLACE,
                SoundSource.BLOCKS, 0.55F, 1.05F);
        return InteractionResult.SUCCESS;
    }

    /** Shift + right-click with empty MAIN hand; newest application first. */
    public static InteractionResult remove(
            Player player, Level level, BlockPos pos, BlockHitResult hit
    ) {
        ItemStack hand = player.getMainHandItem();
        if (!hand.isEmpty() || !player.mayUseItemAt(pos, hit.getDirection(), hand)) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel server)
                || !server.getBlockState(pos).is(ModBlocks.PIZZA_DOUGH)
                || server.getBlockState(pos).getValue(PizzaDoughBlock.STAGE)
                        != PizzaDoughBlock.MAX_STAGE
                || !(server.getBlockEntity(pos) instanceof PizzaDoughBlockEntity pizza)) {
            return InteractionResult.FAIL;
        }

        PizzaComposition current = pizza.composition();
        int zone = PizzaQuarterSelection.selectMask(current, pos, hit.getLocation());
        Optional<PizzaCompositionOperations.Removal> removed =
                PizzaCompositionOperations.removeLast(current, zone);
        if (removed.isPresent()) {
            PizzaCompositionOperations.Removal result = removed.get();
            pizza.setComposition(result.composition());
            ItemStack refund = result.refund();
            if (!refund.isEmpty() && !player.addItem(refund) && !refund.isEmpty()) {
                player.drop(refund, false);
            }
            server.sendBlockUpdated(pos, server.getBlockState(pos),
                    server.getBlockState(pos), Block.UPDATE_CLIENTS);
            server.playSound(null, pos, SoundEvents.WOOL_PLACE,
                    SoundSource.BLOCKS, 0.55F, 0.8F);
            return InteractionResult.SUCCESS;
        }

        // Temporary backwards compatibility for pizzas containing the older
        // whole-mozzarella BlockState proof of concept (not a canonical slice).
        // This flag represents ONE old application covering the whole pizza.
        BlockState state = server.getBlockState(pos);
        if (state.getValue(PizzaDoughBlock.MOZZARELLA)) {
            if (!server.setBlock(pos,
                    state.setValue(PizzaDoughBlock.MOZZARELLA, false),
                    Block.UPDATE_ALL)) {
                return InteractionResult.FAIL;
            }
            ItemStack refund = new ItemStack(ModItems.MOZZARELLA);
            if (!player.addItem(refund) && !refund.isEmpty()) {
                player.drop(refund, false);
            }
            server.playSound(null, pos, SoundEvents.WOOL_PLACE,
                    SoundSource.BLOCKS, 0.55F, 0.8F);
        }
        return InteractionResult.SUCCESS;
    }
}
