package org.tact.features.seasons.handler;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.common.environment.EnvironmentHandler;
import org.tact.common.environment.EnvironmentResult;
import org.tact.features.seasons.component.TemperatureComponent;
import org.tact.features.seasons.config.SeasonsConfig;

import java.util.Map;

public class TemperatureEnvironmentHandler implements EnvironmentHandler {

    private final SeasonsConfig config;

    public TemperatureEnvironmentHandler(SeasonsConfig config) {
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
                player.sendMessage(Message.raw("[Environment] Block " + blockId + " found in proximity."));
                temperatureBonus += (float) Math.log1p(count) * heatValue;
            }
        }
        temperatureBonus = Math.max(config.maxBlockColdBonus, Math.min(temperatureBonus, config.maxBlockHeatBonus));

        temperatureComponent.setEnvironmentModifier(temperatureBonus);
    }
}