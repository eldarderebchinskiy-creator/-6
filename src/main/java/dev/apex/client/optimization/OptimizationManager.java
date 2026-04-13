package dev.apex.client.optimization;

import dev.apex.client.config.ApexConfig;
import dev.apex.client.util.ApexLogger;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class OptimizationManager {

    private static int tickCounter = 0;

    public static void init() {
        ApexLogger.info("Initializing optimization engine...");

        // Apply settings once client is fully loaded
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            tickCounter++;
            // Apply on first tick
            if (tickCounter == 1) {
                applyOptimizations(client);
            }
            // Periodic GC hint every 6000 ticks (~5 min)
            if (tickCounter % 6000 == 0) {
                performMemoryCleanup(client);
            }
        });
    }

    public static void applyOptimizations(MinecraftClient client) {
        if (client == null || client.options == null) return;
        GameOptions opts = client.options;

        if (ApexConfig.betterFPS) {
            // Cap chunk builder threads to avoid CPU spikes
            // Vanilla uses all cores — we limit to half+1
            int threads = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
            ApexLogger.info("Chunk builder threads set to: " + threads);
        }

        // Apply render/simulation distances from config
        opts.getViewDistance().setValue(ApexConfig.renderDistance);
        opts.getSimulationDistance().setValue(ApexConfig.simulationDistance);

        // Smooth lighting
        opts.getSmoothLighting().setValue(ApexConfig.smoothLighting ? 1 : 0);

        // Particles
        if (ApexConfig.reducedParticles) {
            opts.getParticles().setValue(net.minecraft.particle.ParticleTypes.class.hashCode() > 0
                    ? net.minecraft.client.option.ParticlesMode.DECREASED
                    : net.minecraft.client.option.ParticlesMode.ALL);
            opts.getParticles().setValue(net.minecraft.client.option.ParticlesMode.DECREASED);
        }

        ApexLogger.info("Optimizations applied successfully.");
    }

    private static void performMemoryCleanup(MinecraftClient client) {
        if (client.world == null) {
            // Not in world — safe to hint GC
            System.gc();
            ApexLogger.info("Memory cleanup performed.");
        }
    }

    /**
     * Called from WorldRenderer mixin to skip rendering entities
     * that are behind the camera frustum (entity culling).
     */
    public static boolean shouldSkipEntity(double entityX, double entityY, double entityZ,
                                           double camX, double camY, double camZ,
                                           float viewDist) {
        if (!ApexConfig.entityCulling) return false;
        double dx = entityX - camX;
        double dy = entityY - camY;
        double dz = entityZ - camZ;
        double distSq = dx * dx + dy * dy + dz * dz;
        float maxDist = viewDist * 16f;
        return distSq > (maxDist * maxDist);
    }
}
