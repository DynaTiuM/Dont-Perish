package org.tact.features.seasons.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class TemperatureComponent implements Component<EntityStore> {
    private float currentTemperature;
    private float targetTemperature;
    private boolean hasProtection;
    private float damageTimer;

    public TemperatureComponent() {
        this.currentTemperature = 20.0f;
        this.targetTemperature = 20.0f;
        this.hasProtection = false;
        this.damageTimer = 0.0f;
    }

    public float getCurrentTemperature() {
        return this.currentTemperature;
    }
    public void setCurrentTemperature(float temperature) {
        this.currentTemperature = temperature;
    }

    public float getTargetTemperature() {
        return targetTemperature;
    }
    public void setTargetTemperature(float temperature) {
        this.targetTemperature = temperature;
    }

    public boolean hasProtection() {
        return this.hasProtection;
    }
    public void setHasProtection(boolean hasProtection) {
        this.hasProtection = hasProtection;
    }

    public float getDamageTimer() {
        return this.damageTimer;
    }
    public void addDamageTimer(float delta) {
        this.damageTimer += delta;
    }
    public void resetDamageTimer() {
        this.damageTimer = 0.0f;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        TemperatureComponent cloned = new TemperatureComponent();
        cloned.currentTemperature = this.currentTemperature;
        cloned.targetTemperature = this.targetTemperature;
        cloned.hasProtection = this.hasProtection;
        cloned.damageTimer = this.damageTimer;
        return cloned;
    }
}
