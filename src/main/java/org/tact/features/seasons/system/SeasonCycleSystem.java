package org.tact.features.seasons.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.model.Season;
import org.tact.features.seasons.resource.SeasonResource;

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
        SeasonResource data = store.getResource(SeasonResource.TYPE);
        Season currentSeason = data.getCurrentSeason();

        // Progression of the Timer
        data.addSeasonTimer(deltaTime);

        // Duration of the actual season
        float seasonDuration = config.getSeasonDuration(currentSeason.ordinal());

        float progress = Math.min(1.0F, data.getSeasonTimer() / seasonDuration);
        data.setSeasonProgress(progress);

        if(data.getSeasonTimer() >= seasonDuration) {
            Season nextSeason = currentSeason.next();
            data.setCurrentSeason(nextSeason);

            // Preventing the temporal drifting
            float currentTimer = data.getSeasonTimer();
            data.setSeasonTimer(currentTimer - seasonDuration);
            data.setSeasonProgress(0.0F);
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

            store.saveAllResources();

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
        return Query.and(Player.getComponentType());
    }
}
