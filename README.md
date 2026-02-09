# 🌲 Don't Perish

![Status](https://img.shields.io/badge/Status-Active%20Development-orange?style=flat-square)
![License](https://img.shields.io/badge/License-All%20Rights%20Reserved-red?style=flat-square)

**Don't Perish** is a hardcore survival mod for **Hytale**, heavily inspired by the mechanics and dark atmosphere of *Don't Starve*. Survival is no longer a choice: it's a constant struggle against hunger, insanity, and environmental hazards.

> **Project Status:** Under Active Development. This mod is in its early architectural stages and features are subject to change.

---

## 🌟 Key Features

### 🌡️ Advanced Thermodynamics
Gone are simple health bars. Don't Perish introduces a complex body temperature simulation:
- **Dynamic Inertia:** Your body resists temperature changes based on your "comfort zone". Approaching 20°C slows down heat transfer (Homeostasis).
- **Active vs. Passive Protection:**
  - **Isolation (Passive):** Clothes act as a "brake" against the elements.
  - **Active Cooling/Heating:** Use items like **Fans** or **Torches** to forcefully alter your target temperature.
- **Environmental Factors:** Temperature is calculated in real-time based on **Season**, **Time of Day**, **Altitude**, **Light Sources** (Campfires, Furnaces), and **Roof Detection**.
- **HUD Integration:** Visual feedback for body temperature and seasonal progress.

### 🏠 Comfort & Proficiency System
Your mental state is just as important as your physical health. The **Comfort Bar** dictates your efficiency in the world.
- **Environmental Coziness:** Comfort regenerates when you are surrounded by **civilization**. Being near tables, benches, beds, and decorations creates a "cozy" aura that restores your Comfort bar.
- **Proficiency Buffs (High Comfort):** A well-rested explorer is a deadly one. High comfort grants bonuses to **Mining Speed**, **Stamina**, and **Combat Strength**.
- **The "Broken" State (<20%):** Neglecting your comfort triggers lethargy, exhaustion, and weakness.

### 🍎 Metabolism & Perishability
Food serves a dual purpose: fueling your body and soothing your mind.
- **Comfort Foods:** High-quality meals provide a burst of Comfort.
- **Risky Eating:** Eating raw or questionable items drains your Comfort bar.
- **Spoilage:** Stockpiling is risky. Every food item has a decay timer and eventually turns into **Rot**.

### 🐕 Companions
- **Baxter:** Your faithful companion, fully animated and ready to follow you through the wilderness (WIP).

### ⏳ Seasonal Cycle
- Full seasonal rotation affecting ambient temperature and day length (crops season growth: WIP).

---

## 🛠 Technical Architecture

This mod is built using a **Modular Feature Architecture** leveraging Hytale's native **ECS**. Instead of a flat structure, every gameplay mechanic is a self-contained module containing its own ECS components, systems, and configuration.

### Project Structure

```text
src/main/java/org/tact/
├── api/                 # Module interfaces (Feature system)
├── commands/            # Global commands
├── common/              # Shared utilities (Environment, UI, Utils)
├── core/                # Core plugin logic
│   ├── config/          # Global configuration
│   ├── registry/        # Centralized registration
│   └── DontPerishPlugin.java # Main Entry Point
├── features/            # Modular Gameplay Features
│   ├── baxter/          # Companion logic
│   ├── comfort/         # Comfort System (UI, Component, Handler)
│   ├── food_decay/      # Spoilage & Rot mechanics
│   ├── hunger/          # Metabolism logic
│   ├── itemStats/       # Custom items attributes
│   ├── seasons/         # Seasonal cycle logic
│   └── temperature/     # Thermodynamics engine
└── services/            # Global services
```
---

## 📚 Documentation
For a deep dive into the mechanics and item stats:

- [📖 Thermodynamics Mechanics Explained](docs/mechanics.md)
- [🧥 Item Stats & Insulation Guide](docs/items.md)

---

## ⚖️ License & Rights
Copyright © 2026 - **All Rights Reserved**.

Although this source code is visible for educational purposes and portfolio demonstration, this project is **NOT open source**.

You are welcome to read the code to understand the logic and learn how to implement similar features yourself. However, you are **strictly prohibited from directly copy-pasting this source code or using these specific assets** in your own projects.

Please refer to the `LICENSE` file for full legal details and restrictions.

---