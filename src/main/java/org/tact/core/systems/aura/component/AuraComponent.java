package org.tact.core.systems.aura.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.core.systems.aura.AuraEmitter;
import org.tact.core.systems.aura.AuraEvent;

public class AuraComponent implements Component<EntityStore>, AuraEmitter {
    public static ComponentType<EntityStore, AuraComponent> TYPE;

    @NullableDecl
    private AuraEvent currentAura = null;

    public void setAura(@NullableDecl AuraEvent aura) {
        this.currentAura = aura;
    }

    @NullableDecl
    @Override
    public AuraEvent getAura(Ref<EntityStore> entityRef) {
        return currentAura;
    }

    @Override
    public Component<EntityStore> clone() {
        return new AuraComponent();
    }

    public static ComponentType<EntityStore, AuraComponent> getComponentType() {
        return TYPE;
    }
}
