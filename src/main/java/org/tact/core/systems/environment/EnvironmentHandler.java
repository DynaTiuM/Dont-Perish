package org.tact.core.systems.environment;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public interface EnvironmentHandler {
    void onEnvironmentScanned(
            Player player,
            Ref<EntityStore> entityStoreRef,
            Store<EntityStore> store,
            EnvironmentResult result,
            float deltaTime
    );
}
