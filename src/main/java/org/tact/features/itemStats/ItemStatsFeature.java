package org.tact.features.itemStats;

import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import org.tact.api.Feature;
import org.tact.features.itemStats.config.ItemStatsConfig;
import org.tact.features.itemStats.interaction.ItemStatsInteraction;
import org.tact.features.itemStats.system.PassiveItemSystem;

public class ItemStatsFeature implements Feature {
    private final ItemStatsConfig config;

    public ItemStatsFeature(ItemStatsConfig config) {
        this.config = config;
    }

    @Override
    public String getId() { return "items"; }

    @Override
    public void registerComponents(JavaPlugin plugin) {

    }

    @Override
    public void registerSystems(JavaPlugin plugin) {

    }

    @Override
    public void registerEvents(JavaPlugin plugin) {

    }

    @Override
    public void enable(JavaPlugin plugin) {
        plugin.getEntityStoreRegistry().registerSystem(new PassiveItemSystem(config));

    }
    @Override
    public boolean isEnabled() {
        return config.enabled;
    }
}