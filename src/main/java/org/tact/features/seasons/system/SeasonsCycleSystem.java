package org.tact.features.seasons.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.common.ui.HudManager;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.model.Season;
import org.tact.features.seasons.resource.SeasonsResource;
import org.tact.features.seasons.ui.SeasonsHud;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SeasonsCycleSystem extends EntityTickingSystem<EntityStore> {
    private final SeasonsConfig config;

    public SeasonsCycleSystem(
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
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        SeasonsResource data = store.getResource(SeasonsResource.TYPE);
        Season currentSeason = data.getCurrentSeason();

        // Progression of the Timer
        data.addSeasonTimer(deltaTime);

        // Duration of the actual season
        float seasonDuration = config.getSeasonDuration(currentSeason.ordinal());

        float progress = Math.min(1.0F, data.getSeasonTimer() / seasonDuration);
        data.setSeasonProgress(progress);

        if(data.getSeasonTimer() >= seasonDuration) {
            changeSeason(data, currentSeason, seasonDuration, commandBuffer, store);
        }

        int globalDay = calculateGlobalDay(store);
        updateHud(player, currentSeason, progress, globalDay);
    }

    private void changeSeason(
            SeasonsResource data,
            Season currentSeason,
            float oldDuration,
            CommandBuffer<EntityStore> commandBuffer,
            Store store
    ) {
        Season nextSeason = currentSeason.next();
        data.setCurrentSeason(nextSeason);

        // Preventing the temporal drifting
        float currentTimer = data.getSeasonTimer();
        data.setSeasonTimer(currentTimer - oldDuration);
        data.setSeasonProgress(0.0F);
        World world = commandBuffer.getExternalData().getWorld();

        applyDayNightCycle(world, nextSeason);

        store.saveAllResources();
        world.getPlayerRefs().forEach(playerRef -> {
            Player player = commandBuffer.getComponent(playerRef.getReference(), Player.getComponentType());
            // TODO: add progress?
            player.sendMessage(Message.raw("Season changed to: " + nextSeason.getDisplayName()));

        });
    }

    private void applyDayNightCycle(World world, Season nextSeason) {
        int baseDay = config.baseDayDurationSeconds;
        int totalCycleDuration = config.baseDayDurationSeconds + config.baseNightDurationSeconds;

        int newDayDuration = Math.round(baseDay * nextSeason.getDayLengthMultiplier());

        int minimumBuffer = 60;

        newDayDuration = Math.min(newDayDuration, totalCycleDuration - minimumBuffer);
        newDayDuration = Math.max(minimumBuffer, newDayDuration);

        int newNightDuration = totalCycleDuration - newDayDuration;

        WorldConfig worldConfig = world.getWorldConfig();
        try {
            setPrivateConfig(worldConfig, "daytimeDurationSecondsOverride", newDayDuration);
            setPrivateConfig(worldConfig, "nighttimeDurationSecondsOverride", newNightDuration);
            worldConfig.markChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int calculateGlobalDay(Store<EntityStore> store) {
        WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());

        if (timeResource == null) return 1;

        Instant currentTime = timeResource.getGameTime();

        long daysSinceBeginning = ChronoUnit.DAYS.between(WorldTimeResource.ZERO_YEAR, currentTime);

        return (int) daysSinceBeginning + 1;
    }

    private void updateHud(
            Player player,
            Season season,
            float progress,
            int globalDay
    ) {

        HudManager.updateChild(player, "seasons", SeasonsHud.class, (hud, builder) -> {
            hud.render(builder, season, progress, globalDay);
        });
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
