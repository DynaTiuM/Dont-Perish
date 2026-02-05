package org.tact.features.food_decay;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import org.tact.api.Feature;
import org.tact.features.food_decay.config.FoodDecayConfig;
import org.tact.features.food_decay.integration.ContainerDecayModifier;
import org.tact.features.food_decay.integration.SeasonalDecayModifier;
import org.tact.features.food_decay.manager.FoodDecayManager;
import org.tact.features.food_decay.system.FoodDecaySystem;

public class FoodDecayFeature implements Feature {
    private final FoodDecayConfig config;

    public FoodDecayFeature(FoodDecayConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "food";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {
        FoodDecayManager decayManager = new FoodDecayManager(config);
        decayManager.addModifier(new SeasonalDecayModifier());
        decayManager.addModifier(new ContainerDecayModifier());

        plugin.getEntityStoreRegistry().registerSystem(new FoodDecaySystem(config, decayManager));
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
    }

    @Override
    public void enable(JavaPlugin plugin) {
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }
}
