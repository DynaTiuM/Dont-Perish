package org.tact.features.hunger.component;


import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class HungerComponent implements Component<EntityStore> {
    private float lerpedHunger;
    private float elapsedTime;
    private float starvingElapsedTime;

    public static ComponentType<EntityStore, HungerComponent> TYPE;

    public HungerComponent() {
        this.lerpedHunger = 0.0F;
        this.elapsedTime = 0.0F;
        this.starvingElapsedTime = 0.0F;
    }

    public HungerComponent(HungerComponent hungerComponent) {
        this.lerpedHunger = hungerComponent.lerpedHunger;
        this.elapsedTime = hungerComponent.elapsedTime;
        this.starvingElapsedTime = hungerComponent.starvingElapsedTime;
    }

    @Nullable
    public Component<EntityStore> clone() {
        return new HungerComponent(this);
    }

    public float getLerpedHunger() {
        return this.lerpedHunger;
    }

    public void setLerpedHunger(float value) {
        this.lerpedHunger = value;
    }

    public float getElapsedTime() {
        return this.elapsedTime;
    }

    public void addElapsedTime(float dt) {
        this.elapsedTime += dt;
    }

    public void resetElapsedTime() {
        this.elapsedTime = 0.0F;
    }

    public float getStarvingElapsedTime() {
        return this.starvingElapsedTime;
    }

    public void addStarvingElapsedTime(float dt) {
        this.starvingElapsedTime += dt;
    }

    public void resetStarvingElapsedTime() {
        this.starvingElapsedTime = 0.0F;
    }

    public static ComponentType<EntityStore, HungerComponent> getComponentType() {
        return TYPE;
    }
}