package dev.italiansdelight.common.item;

import dev.italiansdelight.common.registry.ModDataComponents;
import dev.italiansdelight.common.registry.ModItems;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Rustic wine mechanics for the three quality-bearing wine bottles.
 * Red wine increases duration, white wine increases potency, cherry wine
 * grants absorption. Low-quality wine has Rustic's adverse effects.
 */
public final class WineDrinkEffects {
    private static final int MAX_DURATION = 12000;
    private static final int MAX_AMPLIFIER = 2;
    private static final float MAX_ABSORPTION = 20.0F;

    private WineDrinkEffects() {}

    public static void apply(ItemStack stack, LivingEntity user) {
        if (!(user.level() instanceof ServerLevel server)) return;
        if (!stack.is(ModItems.WINE_BOTTLE)
                && !stack.is(ModItems.WHITE_WINE_BOTTLE)
                && !stack.is(ModItems.CHERRY_WINE_BOTTLE)) return;
        final float q = Math.clamp(stack.getOrDefault(ModDataComponents.WINE_QUALITY, 0), 0, 100) / 100.0F;

        if (stack.is(ModItems.WINE_BOTTLE)) durations(user, q);
        else if (stack.is(ModItems.WHITE_WINE_BOTTLE)) potency(user, q);
        else absorption(server, user, q);
    }

    private static void durations(LivingEntity user, float q) {
        for (MobEffectInstance old : List.copyOf(user.getActiveEffects())) {
            if (!old.getEffect().value().isBeneficial() || old.isInfiniteDuration()) continue;
            int duration = old.getDuration();
            if (q >= 0.5F) {
                if (duration >= MAX_DURATION) continue;
                int bonus = 600 + (int) (2400 * ((q - 0.5F) * 2.0F));
                replace(user, old, Math.min(MAX_DURATION, duration + bonus), old.getAmplifier());
            } else {
                int penalty = (int) (2400 * Math.abs(q - 0.6F));
                replace(user, old, duration - penalty, old.getAmplifier());
            }
        }
        if (q < 0.5F) nausea(user, (int) (6000 * (1.0F - q)));
    }

    private static void potency(LivingEntity user, float q) {
        for (MobEffectInstance old : List.copyOf(user.getActiveEffects())) {
            if (!old.getEffect().value().isBeneficial()) continue;
            if (q >= 0.5F && old.getAmplifier() < MAX_AMPLIFIER) {
                replace(user, old, old.getDuration(), old.getAmplifier() + 1);
            } else if (q < 0.5F) {
                replace(user, old, old.getDuration(), old.getAmplifier() - 1);
            }
        }
        if (q < 0.5F) nausea(user, (int) (6000 * Math.max(1.0F - q, 0.25F)));
    }

    private static void absorption(ServerLevel server, LivingEntity user, float q) {
        if (q >= 0.5F) {
            float bonus = 10.0F * Math.max((q - 0.5F) * 2.0F, 0.0F);
            user.setAbsorptionAmount(Math.max(user.getAbsorptionAmount(),
                    Math.min(MAX_ABSORPTION, user.getAbsorptionAmount() + bonus)));
        } else {
            float damage = 10.0F * Math.max(Math.abs(q - 0.5F) + 0.1F, 0.25F);
            user.hurtServer(server, user.damageSources().magic(), damage);
            nausea(user, (int) (6000 * Math.max(1.0F - q, 0.25F)));
        }
    }

    private static void replace(LivingEntity user, MobEffectInstance old, int duration, int amplifier) {
        user.removeEffect(old.getEffect());
        if (duration > 0 && amplifier >= 0) {
            user.addEffect(new MobEffectInstance(old.getEffect(), duration, amplifier,
                    old.isAmbient(), old.isVisible(), old.showIcon()));
        }
    }

    private static void nausea(LivingEntity user, int duration) {
        user.addEffect(new MobEffectInstance(MobEffects.NAUSEA, duration, 0, false, false));
    }
}
