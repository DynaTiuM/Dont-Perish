package org.tact.features.temperature.handler;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.common.environment.EnvironmentHandler;
import org.tact.common.environment.EnvironmentResult;
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

        float temperatureBonus = 0.0F;

        for (Map.Entry<String, Integer> entry : result.getBlockCounts().entrySet()) {
            String blockId = entry.getKey();
            int count = entry.getValue();

            float heatValue = config.getBlockTemperature(blockId);

            if (heatValue != 0.0F) {
                temperatureBonus += (float) Math.log1p(count) * heatValue;
            }
        }
        temperatureBonus = Math.max(config.maxBlockColdBonus, Math.min(temperatureBonus, config.maxBlockHeatBonus));

        temperatureComponent.setEnvironmentModifier(temperatureBonus);
    }
}