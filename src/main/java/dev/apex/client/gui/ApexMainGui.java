package dev.apex.client.gui;

import dev.apex.client.ApexClient;
import dev.apex.client.config.ApexConfig;
import dev.apex.client.util.FrametimeTracker;
import dev.apex.client.util.MemoryUtil;
import dev.apex.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class ApexMainGui extends Screen {

    private final Screen parent;
    private int selectedTab = 0;
    private long openTime;
    private String saveFlash = null;
    private long saveFlashTime = 0;

    private static final String[] TABS   = { "⚙ Render", "⚡ Perf", "✨ Visual", "ℹ About" };
    private static final int PANEL_W     = 360;
    private static final int PANEL_H     = 240;
    private static final int HEADER_H    = 22;
    private static final int TAB_BAR_H   = 20;
    private static final int CONTENT_TOP = HEADER_H + TAB_BAR_H + 6;

    public ApexMainGui(Screen parent) {
        super(Text.literal("Apex Client"));
        this.parent = parent;
    }

    private int px() { return width  / 2 - PANEL_W / 2; }
    private int py() { return height / 2 - PANEL_H / 2; }

    @Override
    protected void init() {
        openTime = System.currentTimeMillis();
        rebuildButtons();
    }

    private void rebuildButtons() {
        clearChildren();
        int px = px(), py = py();
        int tabW = PANEL_W / TABS.length;

        // Tab buttons
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            addDrawableChild(ButtonWidget.builder(Text.literal(TABS[i]), b -> {
                selectedTab = idx;
                rebuildButtons();
            }).dimensions(px + i * tabW + 1, py + HEADER_H + 1, tabW - 2, TAB_BAR_H - 2).build());
        }

        int bx  = px + 12;
        int by  = py + CONTENT_TOP;
        int bw  = 156;
        int gap = 20;

        switch (selectedTab) {
            case 0 -> {
                toggle("Fast Render",         bx,       by,           bw, () -> ApexConfig.fastRender,           v -> ApexConfig.fastRender = v);
                toggle("Entity Culling",      bx,       by + gap,     bw, () -> ApexConfig.entityCulling,        v -> ApexConfig.entityCulling = v);
                toggle("Smart Chunks",        bx,       by + gap * 2, bw, () -> ApexConfig.smartChunkLoading,    v -> ApexConfig.smartChunkLoading = v);
                toggle("Smooth Lighting",     bx,       by + gap * 3, bw, () -> ApexConfig.smoothLighting,       v -> ApexConfig.smoothLighting = v);
                toggle("No Entity Shadows",   bx + 178, by,           bw, () -> ApexConfig.disableEntityShadows, v -> ApexConfig.disableEntityShadows = v);
                toggle("No Weather FX",       bx + 178, by + gap,     bw, () -> ApexConfig.noWeather,            v -> ApexConfig.noWeather = v);
            }
            case 1 -> {
                toggle("Better FPS Mode",     bx, by,           bw, () -> ApexConfig.betterFPS,        v -> ApexConfig.betterFPS = v);
                toggle("FPS Counter HUD",     bx, by + gap,     bw, () -> ApexConfig.fpsCounter,       v -> ApexConfig.fpsCounter = v);
                toggle("Optimized HUD",       bx, by + gap * 2, bw, () -> ApexConfig.optimizedHUD,     v -> ApexConfig.optimizedHUD = v);
                toggle("Reduce Particles",    bx, by + gap * 3, bw, () -> ApexConfig.reducedParticles, v -> ApexConfig.reducedParticles = v);
                addDrawableChild(ButtonWidget.builder(
                        Text.literal("§e⟳ §fFree Memory"),
                        b -> {
                            System.gc();
                            saveFlash = "§aMemory freed!";
                            saveFlashTime = System.currentTimeMillis();
                        }
                ).dimensions(bx + 178, by, bw, 16).build());
            }
            case 2 -> {
                toggle("Custom Title Screen", bx, by,           bw, () -> ApexConfig.customTitleScreen, v -> ApexConfig.customTitleScreen = v);
                toggle("Particles",           bx, by + gap,     bw, () -> ApexConfig.particles,         v -> ApexConfig.particles = v);
                toggle("Animated BG",         bx, by + gap * 2, bw, () -> ApexConfig.animatedBackground,v -> ApexConfig.animatedBackground = v);
            }
            case 3 -> {} // drawn in render()
        }

        // Save button
        addDrawableChild(ButtonWidget.builder(Text.literal("§a✔ §fSave"), b -> {
            ApexConfig.save();
            saveFlash = "§aSaved!";
            saveFlashTime = System.currentTimeMillis();
        }).dimensions(px + PANEL_W - 84, py + PANEL_H - 20, 80, 16).build());

        // Close button (X top-right)
        addDrawableChild(ButtonWidget.builder(Text.literal("§c✕"), b -> close())
                .dimensions(px + PANEL_W - 14, py + 4, 10, 12).build());
    }

    // ── Toggle helper
    private interface Get { boolean get(); }
    private interface Set { void set(boolean v); }
    private void toggle(String label, int x, int y, int w, Get g, Set s) {
        boolean on = g.get();
        addDrawableChild(ButtonWidget.builder(
                Text.literal((on ? "§a[ON]  §f" : "§8[OFF] §7") + label),
                b -> { s.set(!g.get()); rebuildButtons(); }
        ).dimensions(x, y, w, 16).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        long now = System.currentTimeMillis();
        long age = now - openTime;

        // Slide-in animation (ease-out cubic, 200ms)
        float progress = Math.min(1f, age / 200f);
        float eased    = 1f - (float) Math.pow(1f - progress, 3);
        int   offsetY  = (int)((1f - eased) * -30);

        int px = px(), py = py() + offsetY;
        int pulse = 140 + (int)(80 * Math.sin(now * 0.003));

        renderBackground(ctx, mouseX, mouseY, delta);

        // ── Background panel
        ctx.fill(px, py, px + PANEL_W, py + PANEL_H, 0xEE07071A);

        // ── Gradient header
        ctx.fillGradient(px, py, px + PANEL_W, py + HEADER_H, 0xFF1B2280, 0xFF5B3FA8);

        // ── Top animated neon line
        ctx.fillGradient(px,              py, px + PANEL_W / 2, py + 2, (pulse<<24)|0x5B6BFF, (pulse<<24)|0xAA44FF);
        ctx.fillGradient(px + PANEL_W/2,  py, px + PANEL_W,     py + 2, (pulse<<24)|0xAA44FF, (pulse<<24)|0x5B6BFF);

        // ── Outer border
        ctx.fill(px,              py,               px + PANEL_W,     py + 1,           0xFF5B6BFF);
        ctx.fill(px,              py + PANEL_H - 1, px + PANEL_W,     py + PANEL_H,     0xFF5B6BFF);
        ctx.fill(px,              py,               px + 1,            py + PANEL_H,     0xFF5B6BFF);
        ctx.fill(px + PANEL_W-1,  py,               px + PANEL_W,     py + PANEL_H,     0xFF5B6BFF);

        // ── Tab bar divider
        ctx.fill(px, py + HEADER_H + TAB_BAR_H, px + PANEL_W, py + HEADER_H + TAB_BAR_H + 1, 0xFF3A3D7A);

        // ── Active tab underline accent
        int tabW = PANEL_W / TABS.length;
        ctx.fillGradient(
                px + selectedTab * tabW, py + HEADER_H + TAB_BAR_H - 2,
                px + selectedTab * tabW + tabW - 2, py + HEADER_H + TAB_BAR_H,
                0xFF7C8AFF, 0xFF5B6BFF);

        // ── Title
        ctx.drawTextWithShadow(textRenderer,
                Text.literal("§b⚡ §fAPEX CLIENT §7v" + ApexClient.VERSION).asOrderedText(),
                px + 6, py + 7, 0xFFFFFF);

        // ── About tab
        if (selectedTab == 3) {
            int tx = px + 16, ty = py + CONTENT_TOP, lh = 11;
            ctx.drawTextWithShadow(textRenderer, Text.literal("§b⚡ Apex Client §fv" + ApexClient.VERSION).asOrderedText(), tx, ty, 0xFFFFFF);
            ctx.drawTextWithShadow(textRenderer, Text.literal("§7Optimization client mod — Fabric 1.21.4").asOrderedText(), tx, ty + lh, 0xFFFFFF);
            ctx.drawTextWithShadow(textRenderer, Text.literal("§f── Features ──────────────────").asOrderedText(), tx, ty + lh * 3, 0xFFFFFF);
            List<String> f = List.of(
                    "Entity culling — skip off-screen entities",
                    "Frametime tracker — min/avg/max display",
                    "Smart chunk loading — reduce CPU spikes",
                    "Entity shadow toggle — big FPS gain",
                    "Animated title screen + star particles",
                    "RAM usage monitor + forced GC button",
                    "Config auto-saved to apexclient.json"
            );
            for (int i = 0; i < f.size(); i++) {
                ctx.drawTextWithShadow(textRenderer,
                        Text.literal("§7• §f" + f.get(i)).asOrderedText(),
                        tx, ty + lh * 4 + i * lh, 0xFFFFFF);
            }
            ctx.drawTextWithShadow(textRenderer,
                    Text.literal("§eRight Shift §7— open this menu").asOrderedText(),
                    tx, ty + lh * 12, 0xFFFFFF);
        }

        // ── Perf stats strip (visible in Perf tab)
        if (selectedTab == 1) {
            int sx = px + 12, sy = py + CONTENT_TOP + 88;
            ctx.fill(sx, sy, px + PANEL_W - 12, sy + 24, 0x88000015);
            ctx.fill(sx, sy, sx + 2, sy + 24, 0xFF5B6BFF);
            ctx.drawTextWithShadow(textRenderer, Text.literal(FrametimeTracker.getSummary()).asOrderedText(), sx + 5, sy + 4, 0xFFFFFF);
            ctx.drawTextWithShadow(textRenderer, Text.literal(MemoryUtil.getSummary()).asOrderedText(), sx + 5, sy + 14, 0xFFFFFF);
        }

        // ── Bottom status bar
        ctx.fill(px, py + PANEL_H - 18, px + PANEL_W, py + PANEL_H - 1, 0xBB050515);
        ctx.drawTextWithShadow(textRenderer,
                Text.literal("§7FPS §a" + net.minecraft.client.MinecraftClient.getInstance().getCurrentFps()
                        + "  §7RAM " + MemoryUtil.getColor() + MemoryUtil.usedMB() + "§7/" + MemoryUtil.maxMB() + "MB").asOrderedText(),
                px + 6, py + PANEL_H - 14, 0xFFFFFF);

        // ── Save flash message
        if (saveFlash != null) {
            long fa = now - saveFlashTime;
            if (fa < 1500) {
                int a = fa < 800 ? 255 : (int)(255 * (1f - (fa - 800) / 700f));
                ctx.drawTextWithShadow(textRenderer,
                        Text.literal(saveFlash).asOrderedText(),
                        px + 8, py + PANEL_H - 14, (a << 24) | 0x00FFFFFF);
            } else saveFlash = null;
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override public boolean shouldPause() { return false; }

    @Override
    public void close() {
        ApexConfig.save();
        assert client != null;
        client.setScreen(parent);
    }
}
