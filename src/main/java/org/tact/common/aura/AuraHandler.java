package org.tact.common.aura;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.List;

public interface AuraHandler {
    void onAurasDetected(
            Player player,
            Ref<EntityStore> entityRef,
            Store<EntityStore> store,
            List<AuraEvent> nearbyAUras,
            float deltaTime
    );
}
