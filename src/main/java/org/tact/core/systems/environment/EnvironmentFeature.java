package org.tact.core.systems.environment;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import org.tact.api.Feature;
import org.tact.core.systems.environment.component.EnvironmentComponent;
import org.tact.core.systems.environment.system.EnvironmentSystem;

public class EnvironmentFeature implements Feature {

    private final EnvironmentRegistry registry;

    public EnvironmentFeature(EnvironmentRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String getId() {
        return "core_environment";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        EnvironmentComponent.TYPE = plugin.getEntityStoreRegistry()
                .registerComponent(EnvironmentComponent.class, EnvironmentComponent::new);
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(
                new EnvironmentSystem(4, 20, registry)
        );
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            var ref = event.getPlayer().getReference();
            var store = ref.getStore();
            if (store.getComponent(ref, EnvironmentComponent.getComponentType()) == null) {
                store.addComponent(ref, EnvironmentComponent.getComponentType());
            }
        });
    }

    @Override
    public void enable(JavaPlugin plugin) {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}