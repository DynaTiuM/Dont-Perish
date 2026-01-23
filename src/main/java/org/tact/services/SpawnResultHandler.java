package org.tact.services;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class SpawnResultHandler {
    private static final SpawnResultHandler INSTANCE = new SpawnResultHandler();

    private SpawnResultHandler() {
    }

    public static SpawnResultHandler getInstance() {
        return INSTANCE;
    }

    public void handleSuccess(Player player, Holder<EntityStore> holder, String modelName) {
        UUIDComponent uuidComponent = holder.getComponent(UUIDComponent.getComponentType());
        if (uuidComponent != null) {
            player.sendMessage(Message.raw(
                "Entity '" + modelName + "' spawned (UUID: " + uuidComponent.getUuid() + ")"
            ));
        }
    }

    public void handleSuccess(Player player, UUIDComponent uuidComponent, String modelName) {
        if (uuidComponent != null) {
            player.sendMessage(Message.raw(
                "Entity '" + modelName + "' spawned (UUID: " + uuidComponent.getUuid() + ")"
            ));
        }
    }

    public void handleError(Player player, Exception e) {
        player.sendMessage(Message.raw("✗ Failed to spawn entity: " + e.getMessage()));
        e.printStackTrace();
    }
}