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
import org.tact.common.ui.HudManager;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.model.Season;
import org.tact.features.seasons.resource.SeasonsResource;
import org.tact.features.seasons.ui.SeasonsHud;
import org.tact.features.temperature.component.TemperatureComponent;

import java.lang.reflect.Field;

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

        updateHud(player, currentSeason, progress);
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
        int baseNight = config.baseNightDurationSeconds;

        int newDayDuration = (int) (baseDay * nextSeason.getDayLengthMultiplier());
        int newNightDuration = (int) (baseNight * (2.0f - nextSeason.getDayLengthMultiplier()));

        WorldConfig worldConfig = world.getWorldConfig();
        try {
            setPrivateConfig(worldConfig, "daytimeDurationSecondsOverride", newDayDuration);
            setPrivateConfig(worldConfig, "nighttimeDurationSecondsOverride", newNightDuration);
            worldConfig.markChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateHud(
            Player player,
            Season season,
            float progress
    ) {

        HudManager.updateChild(player, "seasons", SeasonsHud.class, (hud, builder) -> {
            hud.render(builder, season);
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
