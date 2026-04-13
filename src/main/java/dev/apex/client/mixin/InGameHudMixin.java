package dev.apex.client.mixin;

import dev.apex.client.config.ApexConfig;
import dev.apex.client.util.FrametimeTracker;
import dev.apex.client.util.MemoryUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void apexFrameTrack(DrawContext ctx, float tickDelta, CallbackInfo ci) {
        FrametimeTracker.onFrame();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void apexHudRender(DrawContext ctx, float tickDelta, CallbackInfo ci) {
        if (!ApexConfig.fpsCounter) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.debugEnabled) return; // Already shown in F3

        int fps = mc.getCurrentFps();
        String fpsColor;
        if (fps >= 120)      fpsColor = "§a";
        else if (fps >= 60)  fpsColor = "§e";
        else if (fps >= 30)  fpsColor = "§6";
        else                 fpsColor = "§c";

        // Line 1: FPS
        String line1 = "§7FPS: " + fpsColor + fps +
                (FrametimeTracker.isStuttering() ? " §c[STUTTER]" : "");
        // Line 2: Frametime min/avg/max
        String line2 = FrametimeTracker.getSummary();
        // Line 3: RAM
        String line3 = MemoryUtil.getSummary();

        int panelW = 160;
        int panelH = 34;
        ctx.fill(2, 2, panelW, panelH, 0xAA000012);
        // Left accent bar
        ctx.fill(2, 2, 3, panelH, 0xFF5B6BFF);

        ctx.drawTextWithShadow(mc.textRenderer, Text.literal(line1).asOrderedText(), 6, 4, 0xFFFFFF);
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal(line2).asOrderedText(), 6, 14, 0xFFFFFF);
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal(line3).asOrderedText(), 6, 24, 0xFFFFFF);
    }
}
