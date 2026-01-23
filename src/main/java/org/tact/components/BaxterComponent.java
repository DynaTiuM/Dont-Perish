package org.tact.components;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.DontPerish;

import java.util.UUID;

public class BaxterComponent implements Component<EntityStore> {

    private UUID ownerUUID;
    private float speed = 0.25f;
    private float stopDistance = 2.75f;

    public BaxterComponent(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public BaxterComponent() {}

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public float getSpeed() {
        return speed;
    }

    public float getStopDistance() {
        return stopDistance;
    }

    @Override
    public Component<EntityStore> clone() {
        BaxterComponent comp = new BaxterComponent(this.ownerUUID);
        comp.speed = this.speed;
        comp.stopDistance = this.stopDistance;
        return comp;
    }

    public static ComponentType<EntityStore, BaxterComponent> getComponentType() {
        return DontPerish.baxterComponent;
    }
}
