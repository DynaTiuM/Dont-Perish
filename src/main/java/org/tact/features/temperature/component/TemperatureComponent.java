package org.tact.features.temperature.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class TemperatureComponent implements Component<EntityStore> {
    private float lerpedTemperature;
    private float targetTemperature ;

    private float seasonalModifier;
    private float environmentModifier;

    private boolean hasProtection;
    private float damageTimer;

    public static ComponentType<EntityStore, TemperatureComponent> TYPE;

    public TemperatureComponent() {
        this.lerpedTemperature = 20.0F;
        this.targetTemperature = 20.0F;
        this.seasonalModifier = 0.0F;
        this.environmentModifier = 0.0F;

        this.hasProtection = false;
        this.damageTimer = 0.0F;
    }

    public float getLerpedTemperature() {
        return this.lerpedTemperature;
    }
    public void setLerpedTemperature(float temperature) {
        this.lerpedTemperature = temperature;
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

    public float getEnvironmentModifier() { return environmentModifier; }
    public void setEnvironmentModifier(float modifier) { this.environmentModifier = modifier; }

    public float getSeasonalModifier() {
        return seasonalModifier;
    }
    public void setSeasonalModifier(float seasonalModifier) {
        this.seasonalModifier = seasonalModifier;
    }


    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        TemperatureComponent cloned = new TemperatureComponent();
        cloned.lerpedTemperature = this.lerpedTemperature;
        cloned.targetTemperature = this.targetTemperature;
        cloned.seasonalModifier = this.seasonalModifier;
        cloned.environmentModifier = this.environmentModifier;
        cloned.hasProtection = this.hasProtection;
        cloned.damageTimer = this.damageTimer;
        return cloned;
    }

    public static ComponentType<EntityStore, TemperatureComponent> getComponentType() {
        return TemperatureComponent.TYPE;
    }
}
