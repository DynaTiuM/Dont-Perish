package org.tact.features.seasons.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.tact.features.temperature.component.TemperatureComponent;
import org.tact.features.seasons.resource.SeasonsResource;
import org.tact.features.seasons.config.SeasonsConfig;

public class SeasonsTemperatureBridgeSystem extends EntityTickingSystem<EntityStore> {
    private final SeasonsConfig seasonsConfig;

    public SeasonsTemperatureBridgeSystem(SeasonsConfig seasonsConfig) {
        this.seasonsConfig = seasonsConfig;
    }

    @Override
    public void tick(
        float dt,
        int index,
        ArchetypeChunk<EntityStore> chunk,
        Store<EntityStore> store,
        @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        SeasonsResource seasons = store.getResource(SeasonsResource.TYPE);
        TemperatureComponent temperatureComponent = chunk.getComponent(index, TemperatureComponent.getComponentType());

        if (temperatureComponent != null) {
            float modifier = seasonsConfig.getSeasonTemperatureModifier(seasons.getCurrentSeason().ordinal());

            temperatureComponent.setSeasonalModifier(modifier);
        }
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(TemperatureComponent.getComponentType());
    }
}
