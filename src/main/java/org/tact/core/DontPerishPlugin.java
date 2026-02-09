package org.tact.core;

import com.hypixel.hytale.event.EventBusRegistry;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import org.tact.commands.BaxterCommand;
import org.tact.common.environment.EnvironmentRegistry;
import org.tact.common.environment.EnvironmentScannerSystem;
import org.tact.core.config.FoodDefinition;
import org.tact.core.config.GlobalFoodConfig;
import org.tact.core.config.ModConfig;
import org.tact.core.registry.FeatureRegistry;
import org.tact.features.baxter.BaxterFeature;
import org.tact.features.comfort.ComfortFeature;
import org.tact.features.food_decay.FoodDecayFeature;
import org.tact.features.food_decay.config.FoodDecayConfig;
import org.tact.features.hunger.HungerFeature;
import org.tact.features.hunger.config.HungerConfig;
import org.tact.features.itemStats.ItemStatsFeature;
import org.tact.features.seasons.SeasonsFeature;
import org.tact.features.temperature.TemperatureFeature;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

public class DontPerishPlugin extends JavaPlugin {
    private static final Logger LOGGER = Logger.getLogger(DontPerishPlugin.class.getName());

    private final Config<ModConfig> configWrapper;
    private ModConfig modConfig;
    private FeatureRegistry featureRegistry;
    private EnvironmentRegistry environmentRegistry;

    public DontPerishPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        this.configWrapper = this.withConfig("DontPerish", ModConfig.CODEC);
    }

    @Override
    protected void setup() {
        LOGGER.info("Initializing DontPerish mod...");

        modConfig = configWrapper.get();

        featureRegistry = new FeatureRegistry();
        environmentRegistry = new EnvironmentRegistry();

        getCommandRegistry().registerCommand(new BaxterCommand(modConfig.baxter));

        registerFeatures();

        featureRegistry.getEnabledFeatures().forEach(feature -> {
            LOGGER.info("Setting up feature: " + feature.getId());
            feature.registerComponents(this);
            feature.registerEvents(this);
            feature.registerSystems(this);
        });

        this.configWrapper.save();
    }

    @Override
    protected void start() {
        LOGGER.info("Starting DontPerish mod...");

        featureRegistry.getEnabledFeatures().forEach(feature -> {
            LOGGER.info("Enabling feature: " + feature.getId());
            feature.enable(this);
        });

        getEntityStoreRegistry().registerSystem(
                new EnvironmentScannerSystem(4, 1.0f, environmentRegistry)
        );

        LOGGER.info("DontPerish mod successfully started!");
    }

    private void registerFeatures() {

        dispatchGlobalFoodDefinitions();

        featureRegistry.register(new HungerFeature(modConfig.hunger));
        featureRegistry.register(new SeasonsFeature(modConfig.seasons));

        featureRegistry.register(new BaxterFeature(modConfig.baxter));
        featureRegistry.register(new FoodDecayFeature(modConfig.foodDecay));

        featureRegistry.register(new ItemStatsFeature(modConfig.itemStats));
        featureRegistry.register(new ComfortFeature(modConfig.comfort, modConfig.itemStats, environmentRegistry));
        featureRegistry.register(new TemperatureFeature(modConfig.temperature, modConfig.itemStats, environmentRegistry));
    }

    private void dispatchGlobalFoodDefinitions() {
        GlobalFoodConfig globalConfig = modConfig.globalFood;

        modConfig.foodDecay.decayTimes.clear();
        modConfig.hunger.nutritionValues.clear();

        for (FoodDefinition def : globalConfig.foods) {
            if (def.decayTime > 0) {
                modConfig.foodDecay.decayTimes.put(def.itemId, def.decayTime);
            }

            if (def.hunger > 0) {
                modConfig.hunger.nutritionValues.put(def.itemId,
                        new HungerConfig.NutritionValue(def.hunger, def.comfort));
            }
        }

        LOGGER.info("Successfully dispatched " + globalConfig.foods.size() + " food definitions.");
    }

    public ModConfig getModConfig() {
        return modConfig;
    }
}
