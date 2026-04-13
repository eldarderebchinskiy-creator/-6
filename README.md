# ⚡ Apex Client — Fabric Optimization Mod for Minecraft 1.21.4

A stylish, performance-focused client mod built on Fabric.

---

## Features

### Optimization
| Feature | Description |
|---|---|
| **Fast Render** | Skips redundant render passes |
| **Entity Culling** | Hides entities outside camera frustum |
| **Smart Chunks** | Limits chunk builder threads to avoid CPU spikes |
| **No Entity Shadows** | Removes shadow raycasting (big FPS boost) |
| **No Weather FX** | Disables rain/snow particles |
| **Reduce Particles** | Sets particle mode to Decreased |
| **Smooth Lighting** | Toggle smooth lighting |
| **Better FPS Mode** | JVM-level tweaks on startup |

### HUD
- Real-time **FPS counter** with color coding (green/yellow/orange/red)
- **Frametime tracker** — shows min/avg/max ms over last 120 frames
- **RAM usage** display with color-coded warnings
- Stutter detection — shows `[STUTTER]` when frametime spikes

### Custom Title Screen
- Deep space animated background with drifting star particles
- Animated neon accent lines at top
- Apex Client badge bottom-left, FPS counter bottom-right
- "Apex Client Settings" button added to main menu

### In-Game GUI (Right Shift)
- 4 tabs: Render / Performance / Visual / About
- Slide-in animation on open
- Animated pulsing neon borders
- "Free Memory" button forces GC
- Live FPS + RAM in status bar
- Auto-saves config on close

---

## Installation

1. Install [Fabric Loader 0.16.9+](https://fabricmc.net/use/installer/) for Minecraft 1.21.4
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 1.21.4
3. Place `apexclient-1.0.0.jar` in your `.minecraft/mods/` folder
4. Launch the game!

---

## Building from Source

```bash
# Clone or extract the project
cd apexclient

# Build with Gradle
./gradlew build

# Output JAR will be in:
# build/libs/apexclient-1.0.0.jar
```

**Requirements:**
- Java 21+
- Gradle 8.8 (wrapper included)

---

## Keybinds

| Key | Action |
|---|---|
| **Right Shift** | Open Apex Client GUI |

---

## Config

Saved automatically to `.minecraft/config/apexclient.json`

```json
{
  "fastRender": true,
  "entityCulling": true,
  "smartChunkLoading": true,
  "reducedParticles": false,
  "betterFPS": true,
  "renderDistance": 12,
  "simulationDistance": 8,
  "smoothLighting": true,
  "optimizedHUD": true,
  "fpsCounter": true,
  "noWeather": false,
  "disableEntityShadows": false,
  "customTitleScreen": true,
  "particles": true,
  "animatedBackground": true
}
```

---

## Compatibility

- Minecraft **1.21.4**
- Fabric Loader **0.16.9+**
- Fabric API **0.110.0+**
- Java **21+**
- Compatible with Sodium, Lithium, Iris

---

## License

MIT
