package org.tact.core.systems.aura;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import org.tact.api.Feature;
import org.tact.core.systems.aura.component.AuraComponent;
import org.tact.core.systems.aura.system.AuraSystem;

public class AuraFeature implements Feature {

    private final AuraRegistry registry;

    public AuraFeature(AuraRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String getId() {
        return "core_aura";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        AuraComponent.TYPE = plugin.getEntityStoreRegistry()
                .registerComponent(AuraComponent.class, AuraComponent::new);
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(
                new AuraSystem(20, registry)
        );
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            var ref = event.getPlayer().getReference();
            var store = ref.getStore();
            if (store.getComponent(ref, AuraComponent.getComponentType()) == null) {
                store.addComponent(ref, AuraComponent.getComponentType());
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