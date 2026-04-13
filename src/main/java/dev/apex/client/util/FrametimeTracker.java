package dev.apex.client.util;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Rolling window frametime tracker.
 * Keeps the last 120 frametimes (2 seconds at 60fps)
 * to display min/avg/max and detect stutters.
 */
public class FrametimeTracker {

    private static final int WINDOW = 120;
    private static final Deque<Long> times = new ArrayDeque<>(WINDOW);
    private static long lastFrame = System.nanoTime();

    public static void onFrame() {
        long now = System.nanoTime();
        long delta = now - lastFrame;
        lastFrame = now;

        if (times.size() >= WINDOW) times.pollFirst();
        times.addLast(delta);
    }

    public static float getAvgMs() {
        if (times.isEmpty()) return 0f;
        long sum = 0;
        for (long t : times) sum += t;
        return (sum / times.size()) / 1_000_000f;
    }

    public static float getMinMs() {
        long min = Long.MAX_VALUE;
        for (long t : times) if (t < min) min = t;
        return min == Long.MAX_VALUE ? 0f : min / 1_000_000f;
    }

    public static float getMaxMs() {
        long max = 0;
        for (long t : times) if (t > max) max = t;
        return max / 1_000_000f;
    }

    /** Returns true if last frame was a stutter (>2x average). */
    public static boolean isStuttering() {
        if (times.size() < 10) return false;
        float avg = getAvgMs();
        return getMaxMs() > avg * 2.5f;
    }

    public static String getSummary() {
        return String.format("§7ft: §fmin§7:§a%.1f §favg§7:§e%.1f §fmax§7:§c%.1fms",
                getMinMs(), getAvgMs(), getMaxMs());
    }
}
