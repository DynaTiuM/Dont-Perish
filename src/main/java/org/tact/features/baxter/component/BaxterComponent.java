package org.tact.features.baxter.component;


import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.UUID;

public class BaxterComponent implements Component<EntityStore> {

    private UUID ownerUUID;
    public static ComponentType<EntityStore, BaxterComponent> TYPE;

    public BaxterComponent(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public BaxterComponent() {}

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public Component<EntityStore> clone() {
        BaxterComponent comp = new BaxterComponent(this.ownerUUID);
        return comp;
    }

    public static ComponentType<EntityStore, BaxterComponent> getComponentType() {
        return TYPE;
    }
}
