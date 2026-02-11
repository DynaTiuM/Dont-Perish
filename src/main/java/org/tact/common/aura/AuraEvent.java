package org.tact.common.aura;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class AuraEvent {
    private final String type;
    private final float strength;
    private final float radius;
    private final Ref<EntityStore> source;

    public AuraEvent(
            String type,
            float strength,
            float radius,
            Ref<EntityStore> source
    ) {
        this.type = type;
        this.strength = strength;
        this.radius = radius;
        this.source = source;
    }

    public String getType() { return type; }
    public float getStrength() { return strength; }
    public float getRadius() { return radius; }
    public Ref<EntityStore> getSource() { return source; }

    @Override
    public String toString() {
        return "AuraEvent{type='" + this.type + "', strength=" + this.strength + ", radius=" + this.radius + "}";
    }
}
