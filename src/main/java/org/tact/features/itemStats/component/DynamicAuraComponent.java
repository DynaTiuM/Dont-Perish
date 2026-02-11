package org.tact.features.itemStats.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.common.aura.AuraEmitter;
import org.tact.common.aura.AuraEvent;

public class DynamicAuraComponent implements Component<EntityStore>, AuraEmitter {
    public static ComponentType<EntityStore, DynamicAuraComponent> TYPE;

    @NullableDecl
    private AuraEvent currentAura = null;

    public void setAura(@NullableDecl AuraEvent aura) {
        this.currentAura = aura;
    }

    @NullableDecl
    public AuraEvent getCurrentAura() {
        return currentAura;
    }

    @NullableDecl
    @Override // Implémentation de ton interface AuraEmitter
    public AuraEvent getAura(Ref<EntityStore> entityRef) {
        return currentAura;
    }

    @Override
    public Component<EntityStore> clone() {
        return new DynamicAuraComponent();
    }

    public static ComponentType<EntityStore, DynamicAuraComponent> getComponentType() {
        return TYPE;
    }
}
