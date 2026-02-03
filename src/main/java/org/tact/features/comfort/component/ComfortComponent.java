package org.tact.features.comfort.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class ComfortComponent implements Component<EntityStore>{
    private float environmentalGain;

    public static ComponentType<EntityStore, ComfortComponent> TYPE;

    private transient float lastAppliedBonus = -1.0f;

    public ComfortComponent() {
        this.environmentalGain = 0.0F;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        ComfortComponent cloned = new ComfortComponent();
        cloned.environmentalGain = this.environmentalGain;
        return cloned;
    }


    public float getEnvironmentalGain() {
        return this.environmentalGain;
    }
    public void setEnvironmentalGain(float gain) {
        this.environmentalGain = gain;
    }

    public float getLastAppliedBonus() { return lastAppliedBonus; }
    public void setLastAppliedBonus(float v) { this.lastAppliedBonus = v; }

    public static ComponentType<EntityStore, ComfortComponent> getComponentType() {
        return TYPE;
    }
}