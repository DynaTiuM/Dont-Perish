package org.tact.features.comfort.handler;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.common.environment.EnvironmentHandler;
import org.tact.common.environment.EnvironmentResult;
import org.tact.features.comfort.component.ComfortComponent;
import org.tact.features.comfort.config.ComfortConfig;

import java.util.Map;

public class ComfortEnvironmentHandler implements EnvironmentHandler {
    private final ComfortConfig config;

    public ComfortEnvironmentHandler(ComfortConfig config) {
        this.config = config;
    }

    @Override
    public void onEnvironmentScanned(
            Player player,
            Ref<EntityStore> entityStoreRef,
            Store<EntityStore> store,
            EnvironmentResult result,
            float deltaTime
    ) {
        ComfortComponent comfortComponent = store.getComponent(entityStoreRef, ComfortComponent.getComponentType());
        if(comfortComponent == null) return;

        float totalGain = 0.0F;

        for(Map.Entry<String, Integer> entry: result.getBlockCounts().entrySet()) {
            String blockId = entry.getKey();
            int count = entry.getValue();

            float comfortValue = config.getBlockComfort(blockId);

            if(comfortValue != 0.0F) {
                totalGain += (float) Math.log1p(count) * comfortValue;
            }
        }

        comfortComponent.setEnvironmentalGain(totalGain);
    }
}
