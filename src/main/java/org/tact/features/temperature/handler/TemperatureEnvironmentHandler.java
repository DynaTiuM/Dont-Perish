package org.tact.features.temperature.handler;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.core.systems.environment.EnvironmentHandler;
import org.tact.core.systems.environment.EnvironmentResult;
import org.tact.common.util.TimeUtil;
import org.tact.features.seasons.model.Season;
import org.tact.features.seasons.resource.SeasonsResource;
import org.tact.features.temperature.component.TemperatureComponent;
import org.tact.features.temperature.config.TemperatureConfig;

import java.util.Map;

public class TemperatureEnvironmentHandler implements EnvironmentHandler {

    private final TemperatureConfig config;

    public TemperatureEnvironmentHandler(TemperatureConfig config) {
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

        float totalModifier = 0.0F;

        for (Map.Entry<String, Integer> entry : result.getBlockCounts().entrySet()) {
            String blockId = entry.getKey();
            int count = entry.getValue();

            float heatValue = config.getBlockTemperature(blockId);

            if (heatValue != 0.0F) {
                totalModifier  += (float) Math.log1p(count) * heatValue;
            }
        }

        String floorBlock = result.getBlockUnderFeet();
        float floorValue = config.getFloorTemperature(floorBlock);

        if (floorValue != 0.0F) {
            totalModifier += floorValue;
        }

        WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());
        SeasonsResource seasonData = store.getResource(SeasonsResource.TYPE);

        float dayLengthMultiplier;

        Season currentSeason = seasonData.getCurrentSeason();
        dayLengthMultiplier = currentSeason.getDayLengthMultiplier();
        if (!result.isUnderRoof()) {
            float sunIntensity = calculateSunIntensity(timeResource, dayLengthMultiplier);
            if (sunIntensity > 0) {
                totalModifier += sunIntensity;
            }
        }

        totalModifier = Math.max(config.maxBlockColdBonus, Math.min(totalModifier, config.maxBlockHeatBonus));

        temperatureComponent.setEnvironmentModifier(totalModifier);
    }

    private float calculateSunIntensity(WorldTimeResource timeResource, float dayLengthMultiplier) {
        if (timeResource == null) return 0.0F;

        float preciseHour = TimeUtil.getPreciseHour(timeResource);
        float cycleFactor = TimeUtil.getSeasonalDayCycleFactor(preciseHour, dayLengthMultiplier);

        return Math.max(0.0F, cycleFactor  * config.sunExposureHeat * dayLengthMultiplier);
    }
}