package org.tact.features.baxter.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.features.baxter.component.BaxterComponent;

import javax.annotation.Nonnull;

public class BaxterInteractionSystem extends EntityTickingSystem<EntityStore> {

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(BaxterComponent.getComponentType());
    }

    @Override
    public void tick(float dt, int index,
                     @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                     @Nonnull Store<EntityStore> store,
                     @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);

        setupInteraction(commandBuffer, ref);
    }

    private void setupInteraction(CommandBuffer<EntityStore> commandBuffer, Ref<EntityStore> ref) {

        commandBuffer.ensureComponent(ref, Interactable.getComponentType());

        Interactions interactions = new Interactions();
        interactions.setInteractionId(InteractionType.Use, "Root_Baxter_Use");
        interactions.setInteractionHint("Open Baxter");

        commandBuffer.putComponent(ref, Interactions.getComponentType(), interactions);
    }
}