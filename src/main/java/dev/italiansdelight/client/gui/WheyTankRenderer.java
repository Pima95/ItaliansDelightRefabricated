package dev.italiansdelight.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/** Draws the same atlas-animated whey in the vat screen and JEI. */
public final class WheyTankRenderer {

    private static final Identifier SPRITE = Identifier.fromNamespaceAndPath(
        "italiansdelight",
        "fluids/whey_still"
    );

    private static final int SURFACE_COLOR = 0xFFF1ECCB;

    private WheyTankRenderer() {}

    public static void draw(
        GuiGraphicsExtractor graphics,
        int x,
        int y,
        int width,
        int height,
        int filledHeight
    ) {
        int level = Math.clamp(filledHeight, 0, height);
        if (level == 0) {
            return;
        }

        int liquidTop = y + height - level;
        graphics.enableScissor(x, liquidTop, x + width, y + height);
        try {
            // The GUI atlas advances the .mcmeta animation. Tile at native
            // 16x16 scale and clip the full tank so changing the amount never
            // stretches the texture or shifts the pattern at the bottom.
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                SPRITE,
                x,
                y,
                width,
                height
            );
            graphics.fill(x, liquidTop, x + width, liquidTop + 1, SURFACE_COLOR);
        } finally {
            // Restore any scissor already active in the containing JEI screen.
            graphics.disableScissor();
        }
    }
}
