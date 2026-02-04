package org.tact.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.projectile.config.StandardPhysicsProvider;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import it.unimi.dsi.fastutil.Pair;
import org.tact.features.baxter.component.BaxterComponent;
import org.tact.features.baxter.component.BaxterInventoryComponent;
import org.tact.features.baxter.config.BaxterConfig;

import javax.annotation.Nonnull;
import java.util.UUID;

public class BaxterCommand extends AbstractPlayerCommand {
    private final BaxterConfig config;

    public BaxterCommand(BaxterConfig config) {
        super("baxter", "Test Spawning Baxter with NPC");
        this.config = config;
    }

    @Override
    protected void execute(
            @Nonnull CommandContext commandContext,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world) {
        NPCPlugin npcPlugin = NPCPlugin.get();
        int roleIndex = npcPlugin.getIndex("Baxter");

        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Baxter");
        if (modelAsset == null) modelAsset = ModelAsset.DEBUG;
        Model model = Model.createScaledModel(modelAsset, 1.0f);

        world.execute(() -> {
            TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
            assert transformComponent != null;
            Pair<Ref<EntityStore>, NPCEntity> npcPair = npcPlugin.spawnEntity(
                    store,
                    roleIndex,
                    transformComponent.getPosition(),
                    transformComponent.getRotation(),
                    model,
                    null
            );
            assert npcPair != null;
            Ref<EntityStore> npcRef = npcPair.first();

            store.tryRemoveComponent(npcRef, StandardPhysicsProvider.getComponentType());

            UUIDComponent ownerUUID = store.getComponent(ref, UUIDComponent.getComponentType());
            store.addComponent(npcRef, BaxterComponent.getComponentType(), new BaxterComponent(ownerUUID.getUuid()));
            store.addComponent(npcRef, BaxterInventoryComponent.getComponentType(),
                    new BaxterInventoryComponent(config.inventorySize));

            store.putComponent(npcRef, ActiveAnimationComponent.getComponentType(), new ActiveAnimationComponent());

            store.putComponent(
                    npcRef,
                    TransformComponent.getComponentType(),
                    new TransformComponent(
                            transformComponent.getPosition().clone().add(0, 1, 0),
                            new Vector3f()
                    )
            );
            store.putComponent(npcRef, Velocity.getComponentType(), new Velocity(new Vector3d(0, 0, 0)));
            store.putComponent(npcRef, UUIDComponent.getComponentType(), new UUIDComponent(UUID.randomUUID()));
            if (model.getBoundingBox() != null) {
                store.putComponent(npcRef, BoundingBox.getComponentType(), new BoundingBox(model.getBoundingBox()));
            }
        });
    }
}
