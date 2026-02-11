package org.tact.core.systems.aura;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public interface AuraEmitter {
    @Nullable
    AuraEvent getAura(Ref<EntityStore> entityRef);
}
