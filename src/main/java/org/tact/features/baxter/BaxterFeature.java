package org.tact.features.baxter;

import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import org.tact.api.Feature;
import org.tact.features.baxter.component.BaxterComponent;
import org.tact.features.baxter.component.BaxterInventoryComponent;
import org.tact.features.baxter.config.BaxterConfig;
import org.tact.features.baxter.interaction.BaxterOpenInteraction;
import org.tact.features.baxter.system.BaxterMovementSystem;

public class BaxterFeature implements Feature {
    private final BaxterConfig config;

    public BaxterFeature(BaxterConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "baxter";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        BaxterComponent.TYPE =
                plugin.getEntityStoreRegistry().registerComponent(BaxterComponent.class, BaxterComponent::new);

        BaxterInventoryComponent.TYPE =
            plugin.getEntityStoreRegistry().registerComponent(
                BaxterInventoryComponent.class,
                () -> new BaxterInventoryComponent(config.inventorySize)
        );
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(new BaxterMovementSystem(config));
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getCodecRegistry(Interaction.CODEC).register(
                "OpenBaxter",
                BaxterOpenInteraction.class,
                BaxterOpenInteraction.CODEC
        );
    }

    @Override
    public void enable(JavaPlugin plugin) {
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }
}
