package dev.apex.client.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

public class RenderUtil {

    /** Draw a gradient-filled rounded rect (purely via fill calls). */
    public static void drawGradientRect(DrawContext ctx, int x, int y, int w, int h,
                                        int colorTop, int colorBottom) {
        ctx.fillGradient(x, y, x + w, y + h, colorTop, colorBottom);
    }

    /** Draw a solid semi-transparent dark panel. */
    public static void drawPanel(DrawContext ctx, int x, int y, int w, int h) {
        ctx.fill(x, y, x + w, y + h, 0xCC0A0A12);
        // Border
        ctx.fill(x,         y,         x + w,     y + 1,     0xFF5B6BFF);
        ctx.fill(x,         y + h - 1, x + w,     y + h,     0xFF5B6BFF);
        ctx.fill(x,         y,         x + 1,     y + h,     0xFF5B6BFF);
        ctx.fill(x + w - 1, y,         x + w,     y + h,     0xFF5B6BFF);
    }

    /** Draw a glowing neon line. */
    public static void drawNeonLine(DrawContext ctx, int x1, int y, int x2, int color) {
        ctx.fill(x1, y, x2, y + 1, color);
    }

    /** Lerp color channels. */
    public static int lerpColor(int c1, int c2, float t) {
        int a1 = (c1 >> 24) & 0xFF, r1 = (c1 >> 16) & 0xFF,
            g1 = (c1 >>  8) & 0xFF, b1 =  c1        & 0xFF;
        int a2 = (c2 >> 24) & 0xFF, r2 = (c2 >> 16) & 0xFF,
            g2 = (c2 >>  8) & 0xFF, b2 =  c2        & 0xFF;
        return ((int) MathHelper.lerp(t, a1, a2) << 24) |
               ((int) MathHelper.lerp(t, r1, r2) << 16) |
               ((int) MathHelper.lerp(t, g1, g2) <<  8) |
                (int) MathHelper.lerp(t, b1, b2);
    }

    /** Animated pulse alpha (0..255) using sin wave. */
    public static int pulseAlpha(long tick, float speed) {
        double s = Math.sin(tick * speed);
        return (int) (150 + 80 * s);
    }
}
