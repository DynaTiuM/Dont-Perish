# 🌲 Don't Perish

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

- **Baxter**: Your favorite companion, ready to follow you through every step of your journey.

---

## ⚙️ Development

As Hytale is currently in pre-release, this repository also serves as a foundational resource and a learning guide for community members looking to get started with modding.

**Requirements:**
- HytaleServer.jar library stored inside libs/ 
- Java 17+

---