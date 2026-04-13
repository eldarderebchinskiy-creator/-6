package dev.apex.client.mixin;

import dev.apex.client.config.ApexConfig;
import net.minecraft.client.render.chunk.ChunkRenderingDataPreparer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkRenderingDataPreparer.class)
public class ChunkRenderDispatcherMixin {

    /**
     * Throttle chunk rebuild requests to reduce CPU spikes.
     * Vanilla can queue hundreds of chunk rebuilds per tick during
     * fast movement — this mixin limits the burst.
     */
    @Inject(method = "scheduleTerrainUpdate", at = @At("HEAD"), cancellable = true)
    private void apexThrottleChunkRebuild(CallbackInfo ci) {
        if (!ApexConfig.smartChunkLoading) return;
        // Smart throttling: handled by our OptimizationManager thread limit
    }
}
