package org.tact.features.food_decay;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.inventory.transaction.*;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import org.tact.api.Feature;
import org.tact.features.food_decay.config.FoodDecayConfig;
import org.tact.features.food_decay.manager.FoodDecayManager;
import org.tact.features.food_decay.manager.FoodStackingManager;
import org.tact.features.food_decay.system.*;

public class FoodDecayFeature implements Feature {
    private final FoodDecayConfig config;
    private final FoodStackingManager stackingManager;

    public FoodDecayFeature(FoodDecayConfig config) {
        this.config = config;
        this.stackingManager = new FoodStackingManager(config);
    }

    @Override
    public String getId() {
        return "foodDecay";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {
        FoodDecayManager manager = new FoodDecayManager(config);

        DecayTickControlSystem controller = new DecayTickControlSystem(config, manager);

        PlayerDecaySystem pSystem = new PlayerDecaySystem(manager, controller);
        DroppedItemDecaySystem iSystem = new DroppedItemDecaySystem(manager, controller);
        ContainerDecaySystem cSystem = new ContainerDecaySystem(manager, controller);

        var registry = plugin.getEntityStoreRegistry();
        registry.registerSystem(controller); // En premier !
        registry.registerSystem(pSystem);
        registry.registerSystem(iSystem);
        registry.registerSystem(cSystem);
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            stackingManager.registerPlayer(event.getPlayer());
            stackingManager.clearTransactionCache();
        });
    }

    @Override
    public void enable(JavaPlugin plugin) {
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }
}
