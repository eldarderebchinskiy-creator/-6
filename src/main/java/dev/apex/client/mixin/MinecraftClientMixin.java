package dev.apex.client.mixin;

import dev.apex.client.config.ApexConfig;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    /**
     * Hook into the run() loop to apply JVM-level tweaks once the client is ready.
     * Only runs once.
     */
    private boolean apexStartupDone = false;

    @Inject(method = "run", at = @At("HEAD"))
    private void onRunStart(CallbackInfo ci) {
        if (apexStartupDone) return;
        apexStartupDone = true;

        // Disable verbose OpenGL debug messages in release builds
        System.setProperty("org.lwjgl.util.NoChecks", "true");

        // Prefer lower latency GC pauses
        if (ApexConfig.betterFPS) {
            System.setProperty("java.nio.DirectByteBuffer.cleanerThreshold", "0");
        }
    }
}
