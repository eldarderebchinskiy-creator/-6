package dev.apex.client.mixin;

import dev.apex.client.ApexClient;
import dev.apex.client.config.ApexConfig;
import dev.apex.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends net.minecraft.client.gui.screen.Screen {

    private long initTime;
    // animated particles
    private static final int PARTICLE_COUNT = 40;
    private final float[] px = new float[PARTICLE_COUNT];
    private final float[] py = new float[PARTICLE_COUNT];
    private final float[] pspeed = new float[PARTICLE_COUNT];
    private final float[] palpha = new float[PARTICLE_COUNT];
    private boolean particlesInit = false;

    protected TitleScreenMixin() {
        super(Text.empty());
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void apexInit(CallbackInfo ci) {
        initTime = System.currentTimeMillis();

        if (!particlesInit) {
            java.util.Random rng = new java.util.Random();
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                px[i] = rng.nextFloat() * 1920;
                py[i] = rng.nextFloat() * 1080;
                pspeed[i] = 0.2f + rng.nextFloat() * 0.5f;
                palpha[i] = 50 + rng.nextInt(150);
            }
            particlesInit = true;
        }

        if (!ApexConfig.customTitleScreen) return;

        // Add "Apex" button to open GUI from title screen
        int bx = this.width / 2 - 100;
        int by = this.height / 2 + 72 + 12;
        addDrawableChild(ButtonWidget.builder(
                        Text.literal("⚡ Apex Client Settings"),
                        btn -> client.setScreen(new dev.apex.client.gui.ApexMainGui(this)))
                .dimensions(bx, by, 200, 20)
                .build());
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void apexRenderBackground(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ApexConfig.customTitleScreen) return;

        long elapsed = System.currentTimeMillis() - initTime;
        int w = this.width, h = this.height;

        // Deep space gradient background
        ctx.fillGradient(0, 0, w, h, 0xFF050510, 0xFF0A0A25);

        // Animated stars / particles
        if (ApexConfig.animatedBackground) {
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                py[i] -= pspeed[i];
                if (py[i] < 0) { py[i] = h; px[i] = (float)(Math.random() * w); }
                int a = (int)(palpha[i] * (0.5 + 0.5 * Math.sin(elapsed * 0.002 + i)));
                int color = (a << 24) | 0x8899FF;
                ctx.fill((int)px[i], (int)py[i], (int)px[i]+2, (int)py[i]+2, color);
            }
        }

        // Bottom gradient overlay for content readability
        ctx.fillGradient(0, h - 80, w, h, 0x00000000, 0xCC000015);

        // Top accent line
        int accentA = RenderUtil.pulseAlpha(elapsed, 0.002f);
        ctx.fillGradient(0, 0, w/2, 2, (accentA << 24) | 0x5B6BFF, (accentA << 24) | 0xAA22FF);
        ctx.fillGradient(w/2, 0, w, 2, (accentA << 24) | 0xAA22FF, (accentA << 24) | 0x5B6BFF);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void apexRenderOverlay(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ApexConfig.customTitleScreen) return;

        long elapsed = System.currentTimeMillis() - initTime;
        int w = this.width, h = this.height;

        // Bottom-left: mod badge
        ctx.fill(0, h - 16, 130, h, 0xAA000015);
        ctx.drawTextWithShadow(textRenderer,
                Text.literal("§b⚡ §fApex Client §7v" + ApexClient.VERSION).asOrderedText(),
                4, h - 12, 0xFFFFFF);

        // Bottom-right: FPS
        String fps = "§7FPS §a" + net.minecraft.client.MinecraftClient.getInstance().getCurrentFps();
        int fpsW = textRenderer.getWidth(fps.replaceAll("§.", "")) + 4;
        ctx.fill(w - fpsW - 4, h - 16, w, h, 0xAA000015);
        ctx.drawTextWithShadow(textRenderer, Text.literal(fps).asOrderedText(),
                w - fpsW, h - 12, 0xFFFFFF);
    }
}
