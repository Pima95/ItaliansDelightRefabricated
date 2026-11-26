package dev.italiansdelight.common.item;

import java.util.function.Supplier;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class OrchardBoatItem extends Item {
    private final Supplier<? extends EntityType<? extends Boat>> type;
    public OrchardBoatItem(Supplier<? extends EntityType<? extends Boat>> type, Properties props) {
        super(props);
        this.type = type;
    }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        var hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hit.getType() != HitResult.Type.BLOCK) return InteractionResultHolder.pass(stack);
        Vec3 view = player.getViewVector(1.0F);
        for (Entity entity : level.getEntities(player, player.getBoundingBox().expandTowards(view.scale(5)).inflate(1),
                e -> !e.isSpectator() && e.isPickable())) {
            if (entity.getBoundingBox().inflate(entity.getPickRadius()).contains(player.getEyePosition()))
                return InteractionResultHolder.pass(stack);
        }
        Boat boat = type.get().create(level);
        if (boat == null) return InteractionResultHolder.fail(stack);
        Vec3 pos = hit.getLocation();
        boat.setPos(pos.x, pos.y, pos.z);
        boat.setYRot(player.getYRot());
        if (!level.noCollision(boat, boat.getBoundingBox())) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide) {
            level.addFreshEntity(boat);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, pos);
            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
