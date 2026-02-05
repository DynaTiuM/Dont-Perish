package org.tact.features.food_decay;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import org.tact.api.Feature;
import org.tact.features.food_decay.config.FoodDecayConfig;
import org.tact.features.food_decay.integration.ContainerDecayModifier;
import org.tact.features.food_decay.integration.SeasonalDecayModifier;
import org.tact.features.food_decay.manager.FoodDecayManager;
import org.tact.features.food_decay.manager.FoodStackingManager;
import org.tact.features.food_decay.system.*;

public class FoodDecayFeature implements Feature {
    private final FoodDecayConfig config;
    private final FoodStackingManager stackingManager;
    private final FoodDecayManager decayManager;

    public FoodDecayFeature(FoodDecayConfig config) {
        this.config = config;
        this.stackingManager = new FoodStackingManager(config);

        this.decayManager = new FoodDecayManager(config);
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
        decayManager.addModifier(new SeasonalDecayModifier());
        decayManager.addModifier(new ContainerDecayModifier());

        GlobalTickController controller = new GlobalTickController(config, decayManager, stackingManager);

        PlayerDecaySystem pSystem = new PlayerDecaySystem(decayManager, controller);
        DroppedItemDecaySystem iSystem = new DroppedItemDecaySystem(decayManager, controller);
        ContainerDecaySystem cSystem = new ContainerDecaySystem(decayManager, controller);

        var registry = plugin.getEntityStoreRegistry();
        registry.registerSystem(controller);
        registry.registerSystem(pSystem);
        registry.registerSystem(iSystem);
        registry.registerSystem(cSystem);
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
        plugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            stackingManager.registerPlayer(event.getPlayer());
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
