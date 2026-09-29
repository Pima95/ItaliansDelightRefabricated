package dev.italiansdelight.common.aging;

import net.minecraft.util.StringRepresentable;

/** Only the shelf's appearance is synchronized; progress stays in the BE. */
public enum RackCheeseState implements StringRepresentable {
    EMPTY("empty", null, false),
    FRESH_PARMIGIANO_REGGIANO("fresh_parmigiano_reggiano", AgingCheeseType.PARMIGIANO_REGGIANO, false),
    PARMIGIANO_REGGIANO("parmigiano_reggiano", AgingCheeseType.PARMIGIANO_REGGIANO, true),
    FRESH_PECORINO_ROMANO("fresh_pecorino_romano", AgingCheeseType.PECORINO_ROMANO, false),
    PECORINO_ROMANO("pecorino_romano", AgingCheeseType.PECORINO_ROMANO, true),
    FRESH_GORGONZOLA("fresh_gorgonzola", AgingCheeseType.GORGONZOLA, false),
    GORGONZOLA("gorgonzola", AgingCheeseType.GORGONZOLA, true),
    // Old saves may already contain Provolone. Keep it visible/retrievable,
    // but insertion and further rack processing are no longer permitted.
    FRESH_PROVOLONE("fresh_provolone", AgingCheeseType.PROVOLONE, false),
    PROVOLONE("provolone", AgingCheeseType.PROVOLONE, true);

    private final String name;
    private final AgingCheeseType type;
    private final boolean mature;

    RackCheeseState(String name, AgingCheeseType type, boolean mature) {
        this.name = name;
        this.type = type;
        this.mature = mature;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public AgingCheeseType cheeseType() {
        return type;
    }

    public static RackCheeseState of(AgingCheeseType type, boolean mature) {
        for (RackCheeseState state : values()) {
            if (state.type == type && state.mature == mature) {
                return state;
            }
        }
        return EMPTY;
    }
}
