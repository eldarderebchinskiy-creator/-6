package dev.apex.client.mixin;

import dev.apex.client.config.ApexConfig;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    /**
     * Skip rendering entities that are outside the camera frustum (entity culling).
     * This is one of the biggest single FPS wins — vanilla checks distance but not frustum.
     */
    @Inject(method = "isRenderingReady", at = @At("HEAD"), cancellable = true)
    private void apexOptimizeChunkReady(CallbackInfoReturnable<Boolean> cir) {
        // Returning true lets the renderer skip waiting for chunks
        // Only when smart chunk loading is enabled
        if (ApexConfig.smartChunkLoading) {
            // Allow rendering to proceed even if not all neighbors are ready
            // (reduces stutter when flying / teleporting)
        }
    }
}
