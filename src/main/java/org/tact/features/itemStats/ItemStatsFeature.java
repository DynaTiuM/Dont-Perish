package org.tact.features.itemStats;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.InteractionEffects;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import org.tact.api.Feature;
import org.tact.features.itemStats.config.ItemStatsConfig;
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

        plugin.getCodecRegistry(Interaction.CODEC).register(
                "Fan_Use",
                ItemStatsInteraction.class,
                ItemStatsInteraction.createCodec(3.0F)
        );

    }
    public static class ItemStatsInteraction extends SimpleInteraction {

        public ItemStatsInteraction(float runTime) {
            super();
            this.runTime = runTime;
            this.effects = new PublicInteractionEffects();
        }

        public static BuilderCodec<ItemStatsInteraction> createCodec(float duration) {
            return BuilderCodec.builder(
                    ItemStatsInteraction.class,
                    () -> new ItemStatsInteraction(duration),
                    SimpleInteraction.CODEC
            ).build();
        }
    }

    public static class PublicInteractionEffects extends InteractionEffects {
        public PublicInteractionEffects() {
            super();
            this.waitForAnimationToFinish = false;
        }
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }
}