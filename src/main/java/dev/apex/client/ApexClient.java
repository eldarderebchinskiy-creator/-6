package dev.apex.client;

import dev.apex.client.config.ApexConfig;
import dev.apex.client.optimization.OptimizationManager;
import dev.apex.client.util.ApexLogger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ApexClient implements ClientModInitializer {

    public static final String MOD_ID = "apexclient";
    public static final String MOD_NAME = "Apex Client";
    public static final String VERSION = "1.0.0";

    public static ApexClient INSTANCE;
    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        ApexLogger.info("Initializing " + MOD_NAME + " v" + VERSION);

        // Load config
        ApexConfig.load();

        // Apply optimizations
        OptimizationManager.init();

        // Register keybinding: Right Shift to open GUI
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.apexclient.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.apexclient"
        ));

        // Listen for key press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new dev.apex.client.gui.ApexMainGui(null));
                }
            }
        });

        ApexLogger.info("Apex Client loaded successfully! Press RIGHT SHIFT to open GUI.");
    }

    public static KeyBinding getOpenGuiKey() {
        return openGuiKey;
    }
}
