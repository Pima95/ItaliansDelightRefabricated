package dev.italiansdelight.common.pizza;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.resources.Identifier;

/**
 * Immutable, versioned model for one pizza, independent of world/item/oven/peel.
 *
 * <p>Always contains four PHYSICAL quarters internally, including when the
 * dough has not been cut. Cutting only changes the selectable logical zones;
 * it never remaps or deletes ingredient coverage.
 *
 * <p>Division states: 0=whole, 1=halves, 3=left half quartered,
 * 5=right half quartered, 7=both halves quartered. Orientation is the
 * cardinal direction captured on the first division, not current player yaw.
 *
 * <p>The prototype mozzarella BlockState is intentionally NOT converted
 * here: there is no generalized topping interaction yet.
 */
public record PizzaComposition(
        int version,
        int divisionState,
        String orientation,
        long nextApplicationId,
        long renderSeed,
        List<PizzaToppingApplication> applications
) {
    public static final int FORMAT_VERSION = 1;
    public static final int QUARTER_COUNT = 4;
    public static final int MAX_TOPPINGS_PER_QUARTER = 6;

    public static final Codec<PizzaComposition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("version").forGetter(PizzaComposition::version),
                    Codec.INT.fieldOf("division_state").forGetter(PizzaComposition::divisionState),
                    Codec.STRING.fieldOf("orientation").forGetter(PizzaComposition::orientation),
                    Codec.LONG.fieldOf("next_application_id").forGetter(PizzaComposition::nextApplicationId),
                    Codec.LONG.fieldOf("render_seed").forGetter(PizzaComposition::renderSeed),
                    PizzaToppingApplication.CODEC.listOf().fieldOf("applications")
                            .forGetter(PizzaComposition::applications)
            ).apply(instance, PizzaComposition::new)
    );

    public PizzaComposition {
        Objects.requireNonNull(orientation, "orientation");
        Objects.requireNonNull(applications, "applications");
        if (version != FORMAT_VERSION) {
            throw new IllegalArgumentException("Unsupported pizza composition version " + version);
        }
        if (divisionState != 0 && divisionState != 1 && divisionState != 3
                && divisionState != 5 && divisionState != 7) {
            throw new IllegalArgumentException("Invalid pizza division state: " + divisionState);
        }
        if (!orientation.equals("north") && !orientation.equals("east")
                && !orientation.equals("south") && !orientation.equals("west")) {
            throw new IllegalArgumentException("Invalid pizza division orientation: " + orientation);
        }
        if (nextApplicationId < 1) {
            throw new IllegalArgumentException("Invalid next pizza application ID");
        }

        applications = List.copyOf(applications);
        Set<Long> usedIds = new HashSet<>();
        // Validation is performed on canonical IDs and quarter bitmasks, not
        // broad food tags. Never accept overlapping same-type applications.
        for (int quarter = 0; quarter < QUARTER_COUNT; quarter++) {
            Set<Identifier> distinctTypes = new LinkedHashSet<>();
            int sauceCount = 0;
            for (PizzaToppingApplication application : applications) {
                if (!application.covers(quarter)) {
                    continue;
                }
                if (!distinctTypes.add(application.canonicalId())) {
                    throw new IllegalArgumentException(
                            "Duplicate canonical topping on pizza quarter " + quarter);
                }
                // Unknown canonical IDs from older datapacks remain stored:
                // don't destroy valid saved stacks when a mod is removed.
                PizzaToppingType type =
                        PizzaToppingCatalog.findType(application.canonicalId()).orElse(null);
                if (type != null && type.role() == PizzaToppingType.Role.SAUCE) {
                    sauceCount++;
                }
            }
            if (distinctTypes.size() > MAX_TOPPINGS_PER_QUARTER || sauceCount > 1) {
                throw new IllegalArgumentException(
                        "Too many toppings or sauces on pizza quarter " + quarter);
            }
        }
        long previousId = 0;
        for (PizzaToppingApplication application : applications) {
            if (!usedIds.add(application.sequence())
                    || application.sequence() <= previousId
                    || application.sequence() >= nextApplicationId) {
                throw new IllegalArgumentException(
                        "Duplicate or out-of-order pizza topping application ID");
            }
            previousId = application.sequence();
        }
    }

    public static PizzaComposition empty() {
        return new PizzaComposition(FORMAT_VERSION, 0, "north", 1L, 0L, List.of());
    }

    /** Exact canonical toppings of one quarter, independent of cut state. */
    public Set<Identifier> toppingsForQuarter(int index) {
        if (index < 0 || index >= QUARTER_COUNT) {
            throw new IndexOutOfBoundsException("Pizza quarter must be 0..3");
        }
        Set<Identifier> types = new LinkedHashSet<>();
        for (PizzaToppingApplication application : applications) {
            if (application.covers(index)) {
                types.add(application.canonicalId());
            }
        }
        return Set.copyOf(types);
    }

    /**
     * Change only which logical zones are selectable. Physical topping
     * coverage and application IDs remain exactly as before the cut/rejoin.
     */
    public PizzaComposition withDivision(int newDivisionState, String newOrientation) {
        return new PizzaComposition(
                version, newDivisionState, newOrientation,
                nextApplicationId, renderSeed, applications);
    }

    /**
     * The next implementation can use this for atomic state replacement.
     * No stack consumption happens here; server gameplay must consume the
     * actual source item only after all placement checks pass.
     */
    public PizzaComposition withApplications(List<PizzaToppingApplication> newApplications,
                                              long newNextId) {
        return new PizzaComposition(
                version, divisionState, orientation, newNextId, renderSeed, newApplications);
    }
}
