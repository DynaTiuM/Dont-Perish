package org.tact.features.seasons.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.features.seasons.component.SeasonWorldComponent;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.model.Season;

import java.lang.reflect.Field;

public class SeasonCycleSystem extends EntityTickingSystem<EntityStore> {
    private final SeasonsConfig config;

    public SeasonCycleSystem(
        SeasonsConfig config
    ) {
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
        SeasonWorldComponent seasonComponent = archetypeChunk.getComponent(index, SeasonWorldComponent.getComponentType());
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
            World world = commandBuffer.getExternalData().getWorld();

            int baseDay = config.baseDayDurationSeconds;
            int baseNight = config.baseNightDurationSeconds;

            int newDayDuration = (int) (baseDay * nextSeason.getDayLengthMultiplier());
            int newNightDuration = (int) (baseNight * (2.0f - nextSeason.getDayLengthMultiplier()));
            WorldConfig config = world.getWorldConfig();
            try {
                setPrivateConfig(config, "daytimeDurationSecondsOverride", newDayDuration);
                setPrivateConfig(config, "nighttimeDurationSecondsOverride", newNightDuration);

                config.markChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }

            String msg = "Season changed to: " + nextSeason.getDisplayName();
            world.getPlayerRefs().forEach(player -> {
                player.sendMessage(Message.raw(msg));
            });
        }
    }

    private void setPrivateConfig(Object instance, String fieldName, Object value) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(SeasonWorldComponent.getComponentType());
    }
}
