package dev.apex.client.util;

public class MemoryUtil {

    private static final Runtime RT = Runtime.getRuntime();

    public static long usedMB() {
        return (RT.totalMemory() - RT.freeMemory()) / (1024 * 1024);
    }

    public static long maxMB() {
        return RT.maxMemory() / (1024 * 1024);
    }

    public static int usagePercent() {
        return (int) (usedMB() * 100 / Math.max(1, maxMB()));
    }

    public static String getColor() {
        int pct = usagePercent();
        if (pct < 50) return "§a";
        if (pct < 75) return "§e";
        if (pct < 90) return "§6";
        return "§c";
    }

    public static String getSummary() {
        return String.format("§7RAM: %s%dMB §7/ §f%dMB §7(%d%%)",
                getColor(), usedMB(), maxMB(), usagePercent());
    }
}
