package org.tact.features.seasons.handler;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.common.environment.EnvironmentHandler;
import org.tact.common.environment.EnvironmentResult;
import org.tact.features.seasons.config.SeasonsConfig;
import org.tact.features.seasons.model.Season;
import org.tact.features.seasons.resource.SeasonsResource;
import org.tact.features.temperature.component.TemperatureComponent;

import java.time.LocalDateTime;

public class SeasonsTemperatureHandler implements EnvironmentHandler {

    private final SeasonsConfig config;

    public SeasonsTemperatureHandler(SeasonsConfig config) {
        this.config = config;
    }

    @Override
    public void onEnvironmentScanned(
            Player player,
            Ref<EntityStore> entityRef,
            Store<EntityStore> store,
            EnvironmentResult result,
            float deltaTime
    ) {
        TemperatureComponent temperatureComponent = store.getComponent(entityRef, TemperatureComponent.getComponentType());
        if (temperatureComponent == null) return;

        SeasonsResource seasonData = store.getResource(SeasonsResource.TYPE);
        if (seasonData == null) return;

        Season currentSeason = seasonData.getCurrentSeason();

        // Temperature modified based on the season
        float seasonalTemp = config.getSeasonBaseTemp(currentSeason.ordinal());

        // Modifier based on the hour of the day
        float dayMultiplier = currentSeason.getDayLengthMultiplier();
        float hourCorrection = getTimeModifier(player) * (10.0f * dayMultiplier);

        temperatureComponent.setSeasonalModifier(seasonalTemp + hourCorrection);
    }

    private float getTimeModifier(Player player) {

        if(player.getWorld() != null) {
            return 0.0F;
        }

        Store<EntityStore> store = player.getWorld().getEntityStore().getStore();

        WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());

        LocalDateTime gameTime = timeResource.getGameDateTime();

        int hour = gameTime.getHour();
        int minute = gameTime.getMinute();

        float preciseHour = hour + (minute / 60.0f);

        return (float) Math.cos(((preciseHour - 14.0f) / 24.0f) * 2.0f * Math.PI);
    }
}