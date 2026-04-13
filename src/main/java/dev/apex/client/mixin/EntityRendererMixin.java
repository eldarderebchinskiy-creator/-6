package dev.apex.client.mixin;

import dev.apex.client.config.ApexConfig;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    /**
     * Skip entity shadow rendering when the option is enabled.
     * Shadows are expensive — each entity casts a shadow via raycasting.
     */
    @Inject(method = "getShadowRadius", at = @At("HEAD"), cancellable = true)
    private void apexNoShadows(Entity entity, float tickDelta, CallbackInfoReturnable<Float> cir) {
        if (ApexConfig.disableEntityShadows) {
            cir.setReturnValue(0.0f); // Shadow radius 0 = no shadow drawn
        }
    }
}
