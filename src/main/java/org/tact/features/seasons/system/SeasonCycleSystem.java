package org.tact.features.seasons.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.features.seasons.component.SeasonWorldComponent;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.model.Season;

public class SeasonCycleSystem extends EntityTickingSystem<EntityStore> {
    private final ComponentType<EntityStore, SeasonWorldComponent> seasonComponentType;
    private final SeasonsConfig config;

    public SeasonCycleSystem(
        ComponentType<EntityStore, SeasonWorldComponent> seasonComponentType,
        SeasonsConfig config
    ) {
        this.seasonComponentType = seasonComponentType;
        this.config = config;
    }

    @Override
    public void tick(
            float deltaTime,
            int index,
            @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        SeasonWorldComponent seasonComponent = archetypeChunk.getComponent(index, seasonComponentType);
        if(seasonComponent == null) {
            throw new NullPointerException("Season Component is null! (SeasonCycleSystem Class)");
        }
        Season currentSeason = seasonComponent.getCurrentSeason();

        // Progression of the Timer
        seasonComponent.addSeasonTimer(deltaTime);

        // Duration of the actual season
        float seasonDuration = config.getSeasonDuration(currentSeason.ordinal());

        float progress = Math.min(1.0F, seasonComponent.getSeasonTimer() / seasonDuration);
        seasonComponent.setSeasonProgress(progress);

        if(seasonComponent.getSeasonTimer() >= seasonDuration) {
            Season nextSeason = currentSeason.next();
            seasonComponent.setCurrentSeason(nextSeason);
            seasonComponent.resetSeasonTime();
            seasonComponent.setSeasonProgress(0.0F);

            Player player = archetypeChunk.getComponent(index, Player.getComponentType());
            player.sendMessage(Message.raw("Season changed to: " + nextSeason.getDisplayName()));
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(seasonComponentType);
    }
}
