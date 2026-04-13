package dev.apex.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.apex.client.util.ApexLogger;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;

public class ApexConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("apexclient.json");

    // ── Optimization settings ──
    public static boolean fastRender = true;
    public static boolean entityCulling = true;
    public static boolean smartChunkLoading = true;
    public static boolean reducedParticles = false;
    public static boolean betterFPS = true;
    public static int renderDistance = 12;
    public static int simulationDistance = 8;
    public static boolean smoothLighting = true;
    public static boolean optimizedHUD = true;
    public static boolean fpsCounter = true;
    public static boolean noWeather = false;
    public static boolean disableEntityShadows = false;

    // ── Visual settings ──
    public static boolean customTitleScreen = true;
    public static boolean particles = true;
    public static boolean animatedBackground = true;

    // ── Serialization ──
    private static class ConfigData {
        boolean fastRender, entityCulling, smartChunkLoading,
                reducedParticles, betterFPS, smoothLighting,
                optimizedHUD, fpsCounter, noWeather,
                disableEntityShadows, customTitleScreen,
                particles, animatedBackground;
        int renderDistance, simulationDistance;
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }
        try (Reader r = Files.newBufferedReader(CONFIG_PATH)) {
            ConfigData d = GSON.fromJson(r, ConfigData.class);
            if (d == null) return;
            fastRender            = d.fastRender;
            entityCulling         = d.entityCulling;
            smartChunkLoading     = d.smartChunkLoading;
            reducedParticles      = d.reducedParticles;
            betterFPS             = d.betterFPS;
            renderDistance        = d.renderDistance > 0 ? d.renderDistance : 12;
            simulationDistance    = d.simulationDistance > 0 ? d.simulationDistance : 8;
            smoothLighting        = d.smoothLighting;
            optimizedHUD          = d.optimizedHUD;
            fpsCounter            = d.fpsCounter;
            noWeather             = d.noWeather;
            disableEntityShadows  = d.disableEntityShadows;
            customTitleScreen     = d.customTitleScreen;
            particles             = d.particles;
            animatedBackground    = d.animatedBackground;
        } catch (IOException e) {
            ApexLogger.error("Failed to load config: " + e.getMessage());
        }
    }

    public static void save() {
        ConfigData d = new ConfigData();
        d.fastRender           = fastRender;
        d.entityCulling        = entityCulling;
        d.smartChunkLoading    = smartChunkLoading;
        d.reducedParticles     = reducedParticles;
        d.betterFPS            = betterFPS;
        d.renderDistance       = renderDistance;
        d.simulationDistance   = simulationDistance;
        d.smoothLighting       = smoothLighting;
        d.optimizedHUD         = optimizedHUD;
        d.fpsCounter           = fpsCounter;
        d.noWeather            = noWeather;
        d.disableEntityShadows = disableEntityShadows;
        d.customTitleScreen    = customTitleScreen;
        d.particles            = particles;
        d.animatedBackground   = animatedBackground;
        try (Writer w = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(d, w);
        } catch (IOException e) {
            ApexLogger.error("Failed to save config: " + e.getMessage());
        }
    }
}
