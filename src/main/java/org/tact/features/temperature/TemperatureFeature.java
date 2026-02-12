package org.tact.features.temperature;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.api.Feature;
import org.tact.core.systems.environment.EnvironmentRegistry;
import org.tact.core.systems.environment.component.EnvironmentComponent;
import org.tact.common.ui.HudManager;
import org.tact.features.itemStats.config.ItemStatsConfig;
import org.tact.features.temperature.component.TemperatureComponent;
import org.tact.features.temperature.config.TemperatureConfig;
import org.tact.features.temperature.handler.TemperatureEnvironmentHandler;
import org.tact.features.temperature.system.TemperatureSystem;
import org.tact.features.temperature.ui.TemperatureHud;

public class TemperatureFeature implements Feature {
    private final TemperatureConfig config;
    private final ItemStatsConfig itemConfig;

    private final EnvironmentRegistry environmentRegistry;

    public TemperatureFeature(
        TemperatureConfig config,
        ItemStatsConfig itemConfig,
        EnvironmentRegistry environmentRegistry
    ) {
        this.config = config;
        this.itemConfig = itemConfig;
        this.environmentRegistry = environmentRegistry;
    }

    @Override
    public String getId() {
        return "temperature";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        TemperatureComponent.TYPE = plugin.getEntityStoreRegistry()
                .registerComponent(TemperatureComponent.class, TemperatureComponent::new);
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Player player = event.getPlayer();
            player.getWorld().execute(() -> {
                Ref<EntityStore> ref = player.getReference();
                Store<EntityStore> store = ref.getStore();

                if (store.getComponent(ref, TemperatureComponent.getComponentType()) == null) {
                    store.addComponent(ref, TemperatureComponent.getComponentType());
                }
                if (store.getComponent(ref, EnvironmentComponent.getComponentType()) == null) {
                    store.addComponent(ref, EnvironmentComponent.getComponentType());
                }

                PlayerRef pRef = store.getComponent(ref, PlayerRef.getComponentType());
                HudManager.open(player, pRef, new TemperatureHud(pRef), getId());
            });
        });
    }

    @Override
    public void enable(JavaPlugin plugin) {
        environmentRegistry.register("temperature", new TemperatureEnvironmentHandler(config));

        plugin.getEntityStoreRegistry().registerSystem(
                new TemperatureSystem(config, itemConfig)
        );
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }
}
