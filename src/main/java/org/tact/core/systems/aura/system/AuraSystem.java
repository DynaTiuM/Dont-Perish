package org.tact.core.systems.aura.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.core.systems.aura.AuraEmitter;
import org.tact.core.systems.aura.AuraEvent;
import org.tact.core.systems.aura.AuraHandler;
import org.tact.core.systems.aura.AuraRegistry;
import org.tact.features.comfort.component.ComfortComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuraSystem extends EntityTickingSystem<EntityStore> {

    private final int ticksInterval;
    private final AuraRegistry auraRegistry;

    public AuraSystem(int ticksInterval, AuraRegistry auraRegistry) {
        this.ticksInterval = ticksInterval;
        this.auraRegistry = auraRegistry;
    }

    @Override
    public void tick(
            float deltaTime,
            int index,
            @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);

        if (player == null) return;

        collectAuras(index, entityRef, archetypeChunk);

        long worldTime = player.getWorld().getTick();
        if ((worldTime + index) % this.ticksInterval != 0) {
            return;
        }

        List<AuraEvent> nearbyAuras = findNearbyAuras(entityRef, store);

        if (nearbyAuras.isEmpty()) {
            ComfortComponent comfortComponent = archetypeChunk.getComponent(index, ComfortComponent.getComponentType());
            if (comfortComponent != null) comfortComponent.setAuraGain(0.0F);
        } else {
            for (AuraHandler handler : auraRegistry.getAllHandlers().values()) {
                handler.onAurasDetected(player, entityRef, store, nearbyAuras);
            }
        }
    }

    private void collectAuras(
            int index,
            Ref<EntityStore> entityRef,
            ArchetypeChunk<EntityStore> archetypeChunk
    ) {
        List<AuraEvent> auras = new ArrayList<>();
        Archetype<EntityStore> archetype = archetypeChunk.getArchetype();

        for (int i = archetype.getMinIndex(); i < archetype.length(); i++) {
            ComponentType<EntityStore, ? extends Component<EntityStore>> componentType = archetype.get(i);

            if (componentType != null) {
                Component<EntityStore> component = archetypeChunk.getComponent(index, componentType);

                if (component instanceof AuraEmitter emitter) {
                    AuraEvent aura = emitter.getAura(entityRef);
                    if (aura != null) {
                        auras.add(aura);
                    }
                }
            }
        }

        auraRegistry.setAuras(entityRef, auras);
    }

    private List<AuraEvent> findNearbyAuras(Ref<EntityStore> playerRef, Store<EntityStore> store) {
        List<AuraEvent> nearby = new ArrayList<>();

        TransformComponent playerTransform = store.getComponent(playerRef, TransformComponent.getComponentType());
        if (playerTransform == null) return nearby;

        Vector3d playerPos = playerTransform.getPosition();

        for (Map.Entry<Ref<EntityStore>, List<AuraEvent>> entry : auraRegistry.getAllAuras().entrySet()) {
            Ref<EntityStore> sourceRef = entry.getKey();

            if (sourceRef.equals(playerRef)) continue;

            TransformComponent sourceTransform = store.getComponent(sourceRef, TransformComponent.getComponentType());
            if (sourceTransform == null) continue;

            Vector3d sourcePos = sourceTransform.getPosition();
            double distance = playerPos.distanceTo(sourcePos);

            for (AuraEvent aura : entry.getValue()) {
                if (distance <= aura.getRadius()) {
                    nearby.add(aura);
                }
            }
        }

        return nearby;
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }
}