package org.tact.features.comfort.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class ComfortComponent implements Component<EntityStore>{
    private float currentComfort;
    private float lerpedComfort;
    private float environmentalGain;
    private float elapsedTime;

    public static ComponentType<EntityStore, ComfortComponent> TYPE;

    public ComfortComponent() {
        this.currentComfort = 0.0F;
        this.lerpedComfort = 100.0F;
        this.environmentalGain = 0.0F;
        this.elapsedTime = 0.0F;
    }

    public ComfortComponent(ComfortComponent comfortComponent) {
        this.lerpedComfort = comfortComponent.lerpedComfort;
        this.elapsedTime = comfortComponent.elapsedTime;
    }

    @Nullable
    public Component<EntityStore> clone() {
        return new ComfortComponent(this);
    }

    public float getLerpedComfort() {
        return this.lerpedComfort;
    }

    public void setLerpedComfort(float value) {
        this.lerpedComfort = value;
    }

    public float getCurrentComfort() { return currentComfort; }
    public void setCurrentComfort(float value) { this.currentComfort = value; }

    public float getEnvironmentalGain() {
        return this.environmentalGain;
    }
    public void setEnvironmentalGain(float gain) {
        this.environmentalGain = gain;
    }


    public static ComponentType<EntityStore, ComfortComponent> getComponentType() {
        return TYPE;
    }
}