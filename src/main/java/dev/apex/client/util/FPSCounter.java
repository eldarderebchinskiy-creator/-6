package dev.apex.client.util;

public class FPSCounter {

    private static int fps = 0;
    private static long lastTime = System.currentTimeMillis();
    private static int frameCount = 0;

    public static void onFrameRendered() {
        frameCount++;
        long now = System.currentTimeMillis();
        if (now - lastTime >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastTime = now;
        }
    }

    public static int getFPS() {
        return fps;
    }

    public static String getFPSColor() {
        if (fps >= 120) return "§a";       // green
        if (fps >= 60)  return "§e";       // yellow
        if (fps >= 30)  return "§6";       // orange
        return "§c";                        // red
    }
}
