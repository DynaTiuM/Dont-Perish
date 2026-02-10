package org.tact.features.itemStats.component;

import com.hypixel.hytale.component.Component;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class UsageBufferComponent implements Component<EntityStore> {
    private float accumulatedDamage = 0.0f;
    private float timeSinceLastSync = 0.0f;

    public static ComponentType<EntityStore, UsageBufferComponent> TYPE;

    public float getAccumulatedDamage() { return accumulatedDamage; }
    public void addDamage(float amount) { this.accumulatedDamage += amount; }
    public void consumeDamage(float amount) { this.accumulatedDamage -= amount; }

    public void addSyncTime(float dt) { this.timeSinceLastSync += dt; }
    public boolean shouldSync() { return timeSinceLastSync >= 0.5f; } // Sync toutes les 0.5s
    public void resetSyncTimer() { this.timeSinceLastSync = 0; }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new UsageBufferComponent();
    }

    public static ComponentType<EntityStore, UsageBufferComponent> getComponentType() {
        return UsageBufferComponent.TYPE;
    }
}