package org.tact.features.music.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.core.systems.aura.AuraEmitter;
import org.tact.core.systems.aura.AuraEvent;

import javax.annotation.Nullable;

public class MusicComponent implements Component<EntityStore>, AuraEmitter {

    public static ComponentType<EntityStore, MusicComponent> TYPE;

    private boolean isPlaying = false;
    private float strength = 0.8f;
    private float radius = 10.0f;

    public MusicComponent() {
    }

    public void startPlaying(float strength) {
        this.isPlaying = true;
        this.strength = strength;
    }

    public void stopPlaying() {
        this.isPlaying = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    @Nullable
    @Override
    public AuraEvent getAura(Ref<EntityStore> entityRef) {
        if (!isPlaying) return null;

        return new AuraEvent("music", strength, radius, entityRef);
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        MusicComponent cloned = new MusicComponent();
        cloned.isPlaying = this.isPlaying;
        cloned.strength = this.strength;
        cloned.radius = this.radius;
        return cloned;
    }

    public static ComponentType<EntityStore, MusicComponent> getComponentType() {
        return TYPE;
    }
}