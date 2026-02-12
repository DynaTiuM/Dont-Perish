package org.tact.features.seasons.system;

import com.hypixel.hytale.builtin.weather.resources.WeatherResource;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.features.seasons.model.Season;
import org.tact.features.seasons.resource.SeasonsResource;

import java.util.Random;

public class SeasonsWeatherSystem extends EntityTickingSystem<EntityStore> {

    private final Random random = new Random();

    private static final float WEATHER_CHECK_INTERVAL = 240.0F;


    public SeasonsWeatherSystem() {}

    @Override
    public void tick(
            float deltaTime,
            int index,
            @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        if(index != 0) return;
        WeatherResource weatherResource = store.getResource(WeatherResource.getResourceType());
        SeasonsResource seasonsResource = store.getResource(SeasonsResource.TYPE);

        if (weatherResource.getForcedWeatherIndex() != 0 && !seasonsResource.isWeatherForced()) {
            System.out.println("[Weather] Forced Weather Detected (Season Transition). Taking control.");
            seasonsResource.setWeatherForced(true);
            seasonsResource.setWeatherForcedDuration(60.0F);
        }
        if (seasonsResource.isWeatherForced()) {
            seasonsResource.decreaseWeatherForcedDuration(deltaTime);
            if (seasonsResource.getWeatherForcedDuration() <= 0) {
                resetWeather(store, seasonsResource);
            }
            return;
        }

        seasonsResource.addWeatherCheckTimer(deltaTime);

        if (seasonsResource.getWeatherCheckTimer() >= WEATHER_CHECK_INTERVAL) {
            seasonsResource.setWeatherCheckTimer(0.0F);
            tryTriggerSeasonalWeather(store, seasonsResource);
        }

    }

    private void tryTriggerSeasonalWeather(Store<EntityStore> store, SeasonsResource seasonsResource) {
        if (seasonsResource.isWeatherForced()) return;

        WeatherResource weatherResource = store.getResource(WeatherResource.getResourceType());

        Season currentSeason = seasonsResource.getCurrentSeason();

        float roll = random.nextFloat();

        if(roll < currentSeason.getSnowChance()) {
            forceWeather(weatherResource, seasonsResource, "Zone3_Snow", 300.0F);
        }
        else if(roll < currentSeason.getSnowChance() + currentSeason.getStormChance()) {
            forceWeather(weatherResource, seasonsResource, "Zone1_Storm", 300.0F);
        }
        else if(roll < currentSeason.getSnowChance() + currentSeason.getStormChance() + currentSeason.getRainChance()) {
            forceWeather(weatherResource,seasonsResource, "Zone1_Rain", 300.0F);
        }
    }

    private void forceWeather(
            WeatherResource resource,
            SeasonsResource seasonsResource,
            String weatherId,
            float duration
    ) {
        System.out.println("[Weather] Forcing " + weatherId + " for " + duration + "s");

        resource.setForcedWeather(weatherId);

        seasonsResource.setWeatherForced(true);
        seasonsResource.setWeatherForcedDuration(duration);
    }

    private void resetWeather(Store<EntityStore> store, SeasonsResource seasonsResource) {
        WeatherResource resource = store.getResource(WeatherResource.getResourceType());

        System.out.println("[Weather] Returning to natural cycle");
        resource.setForcedWeather(null);

        seasonsResource.setWeatherForced(false);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }
}
