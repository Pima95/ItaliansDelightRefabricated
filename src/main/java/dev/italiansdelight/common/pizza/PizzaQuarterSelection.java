package dev.italiansdelight.common.pizza;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

/**
 * One logical hit region on a pizza is mapped onto four stable PHYSICAL bits.
 *
 * <p>Bit order, relative to the first saved facing direction:
 * 0 = left/back, 1 = left/front, 2 = right/back, 3 = right/front.
 * Whole pizza: 0b1111; left half: 0b0011; right half: 0b1100.
 *
 * <p>Cut lines do not have hitboxes. At u/v = 0 (with a 1/1024-block
 * tolerance), right/front wins, including exactly at the pizza's center.
 */
public final class PizzaQuarterSelection {
    public static final double EPSILON = 1.0 / 1024.0;
    public static final int ALL_QUARTERS = 0b1111;
    public static final int LEFT_HALF = 0b0011;
    public static final int RIGHT_HALF = 0b1100;

    private PizzaQuarterSelection() {
    }

    /**
     * Captures the closest horizontal cardinal facing when a player makes
     * their FIRST split. At exact diagonal headings the next clockwise
     * cardinal wins: N/E -> E, E/S -> S, S/W -> W, W/N -> N.
     *
     * Minecraft yaw increases S -> W -> N -> E.
     */
    public static String nearestClockwiseFacing(float playerYaw) {
        int sector = Math.floorMod((int) Math.floor(((double) playerYaw + 45.0) / 90.0), 4);
        return switch (sector) {
            case 0 -> "south";
            case 1 -> "west";
            case 2 -> "north";
            default -> "east";
        };
    }

    /** The real block hit position is used; player facing after the cut isn't. */
    public static int selectMask(PizzaComposition pizza, BlockPos pos, Vec3 hitLocation) {
        return selectMask(pizza, hitLocation.x - pos.getX(), hitLocation.z - pos.getZ());
    }

    /**
     * Select precisely one visible logical zone, represented by its coverage
     * of physical quarters. localX/localZ are offsets 0..1 inside the block.
     */
    public static int selectMask(PizzaComposition pizza, double localX, double localZ) {
        if (pizza.divisionState() == 0) {
            return ALL_QUARTERS;
        }
        int quarter = physicalQuarter(pizza.orientation(), localX, localZ);
        boolean right = quarter >= 2;
        int halfMask = right ? RIGHT_HALF : LEFT_HALF;
        int dividedFlag = right ? 4 : 2;
        return (pizza.divisionState() & dividedFlag) != 0
                ? 1 << quarter : halfMask;
    }

    /** Return the stable physical quarter index, even on an uncut pizza. */
    public static int physicalQuarter(String orientation, double localX, double localZ) {
        Direction forward = switch (orientation) {
            case "north" -> Direction.NORTH;
            case "east" -> Direction.EAST;
            case "south" -> Direction.SOUTH;
            case "west" -> Direction.WEST;
            default -> throw new IllegalArgumentException("Invalid pizza orientation: " + orientation);
        };
        double x = localX - 0.5;
        double z = localZ - 0.5;

        // Right = (-forward.z, forward.x); front = forward.
        double u = x * -forward.getStepZ() + z * forward.getStepX();
        double v = x * forward.getStepX() + z * forward.getStepZ();
        int side = u >= -EPSILON ? 2 : 0;
        int front = v >= -EPSILON ? 1 : 0;
        return side + front;
    }

    /**
     * Split one logical region without touching existing topping coverage.
     * Returns the original object if that half is already quartered.
     */
    public static PizzaComposition split(PizzaComposition pizza, int targetMask, String firstFacing) {
        if (pizza.divisionState() == 0) {
            return pizza.withDivision(1, firstFacing);
        }
        if ((targetMask & LEFT_HALF) != 0 && (targetMask & RIGHT_HALF) == 0) {
            return (pizza.divisionState() & 2) != 0
                    ? pizza : pizza.withDivision(pizza.divisionState() | 2, pizza.orientation());
        }
        if ((targetMask & RIGHT_HALF) != 0 && (targetMask & LEFT_HALF) == 0) {
            return (pizza.divisionState() & 4) != 0
                    ? pizza : pizza.withDivision(pizza.divisionState() | 4, pizza.orientation());
        }
        return pizza;
    }

    /**
     * Rejoin a selected quarter's half first. Both halves must be uncut to
     * rejoin the whole pizza. No topping mask or chronology is changed.
     */
    public static PizzaComposition join(PizzaComposition pizza, int targetMask) {
        if (pizza.divisionState() == 0) {
            return pizza;
        }
        if ((targetMask & LEFT_HALF) != 0 && (targetMask & RIGHT_HALF) == 0
                && (pizza.divisionState() & 2) != 0) {
            return pizza.withDivision(pizza.divisionState() & ~2, pizza.orientation());
        }
        if ((targetMask & RIGHT_HALF) != 0 && (targetMask & LEFT_HALF) == 0
                && (pizza.divisionState() & 4) != 0) {
            return pizza.withDivision(pizza.divisionState() & ~4, pizza.orientation());
        }
        return pizza.divisionState() == 1
                ? pizza.withDivision(0, pizza.orientation()) : pizza;
    }
}
