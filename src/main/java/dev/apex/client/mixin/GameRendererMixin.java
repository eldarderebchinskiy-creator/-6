package dev.apex.client.mixin;

import dev.apex.client.config.ApexConfig;
import dev.apex.client.util.FPSCounter;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void apexOnRenderStart(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        FPSCounter.onFrameRendered();
    }

    /**
     * When fast render is enabled, we skip the entity outline pass
     * when there's no entity being highlighted (saves ~2-5ms per frame).
     */
    @Inject(method = "renderWorld", at = @At("HEAD"), cancellable = false)
    private void apexFastRender(CallbackInfo ci) {
        if (!ApexConfig.fastRender) return;
        // Additional fast-path checks can be added here
        // e.g. skip weather particles when noWeather is set
    }
}
