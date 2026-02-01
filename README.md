# 🌲 Don't Perish

![Status](https://img.shields.io/badge/Status-In%20Development-orange) ![License](https://img.shields.io/badge/License-All%20Rights%20Reserved-red)

**Don't Perish** is a mod for **Hytale**, heavily inspired by the mechanics and dark atmosphere of the game *Don't Starve*. Survival is no longer a choice: it's a constant struggle against hunger, insanity, health and environmental hazards.

> **Project Status:** Under Active Development. This mod is in its VERY early architectural stages.

---

## 🛠 Technical Architecture

This mod is built from the ground up leveraging Hytale's native **ECS (Entity Component System)**.

### Project Structure

The codebase is organized to maintain a strict separation between assets and server-side logic:
* **`java/`** – The core logic of the mod:
    * `commands/` – Custom chat commands for debugging and gameplay.
    * `components/` – Pure data containers attached to entities.
    * `interactions/` – Logic for player-world interaction.
    * `services/` – Managers for data and logic external to the core ECS flow.
    * `systems/` – The brains of the mod. These process components every tick.
    * **`DontPerish.java`** – The main entry point and mod initialization class.

* **`resources/`** – Contains all textures, 3D models, animations, and server-side data definitions.

---

## 🕯 Currently In Progress

- **Baxter**: Your favorite companion, ready to follow you through every step of your journey
- **Seasons**: Seasonal system affecting gameplay in various ways (temperature, length of days, etc.)
- **Hunger**: A Hunger bar forcing the player to manage their food consumption

---

## ⚙️ Development

As Hytale is currently in pre-release, this repository also serves as a foundational resource and a learning guide for community members looking to get started with modding.

⚠️ **Important Note on Licensing:**  
Although this source code is visible for educational purposes, this project is released under an **All Rights Reserved** license.

You are welcome to read the code to understand the logic and learn how to implement similar features yourself. However, you are **strictly prohibited from directly copy-pasting this source code or using these specific assets** in your own projects.

Please refer to the `LICENSE` file for full legal details and restrictions.

---