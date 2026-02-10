package org.tact.features.itemStats.component;

import com.hypixel.hytale.component.Component;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.features.itemStats.model.ItemStatSnapshot;

import java.util.HashMap;
import java.util.Map;

public class UsageBufferComponent implements Component<EntityStore> {

    private final Map<String, Float> accumulatedDamages = new HashMap<>();
    private ItemStatSnapshot lastSnapshot = new ItemStatSnapshot();
    private float timeSinceLastSync = 0.0f;

    public static ComponentType<EntityStore, UsageBufferComponent> TYPE;

    public float getDamage(String key) {
        return accumulatedDamages.getOrDefault(key, 0.0f);
    }
    public void addDamage(String key, float amount) {
        accumulatedDamages.put(key, getDamage(key) + amount);
    }
    public void consumeDamage(String key, float amount) {
        accumulatedDamages.put(key, Math.max(0, getDamage(key) - amount));
    }
    public void addSyncTime(float dt) { this.timeSinceLastSync += dt; }
    public boolean shouldSync() { return timeSinceLastSync >= 2.0F; }
    public void resetSyncTimer() { this.timeSinceLastSync = 0; }

    public ItemStatSnapshot getLastSnapshot() { return lastSnapshot; }
    public void setLastSnapshot(ItemStatSnapshot snapshot) { this.lastSnapshot = snapshot; }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new UsageBufferComponent();
    }

    public static ComponentType<EntityStore, UsageBufferComponent> getComponentType() {
        return UsageBufferComponent.TYPE;
    }
}