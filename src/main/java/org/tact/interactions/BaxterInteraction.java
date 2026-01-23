package org.tact.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.tact.components.BaxterComponent;
import org.tact.services.SpawnResultHandler;

import java.util.UUID;

public class BaxterInteraction extends SimpleInstantInteraction {

    private final SpawnResultHandler resultHandler;
    protected BaxterInteraction() {
        resultHandler = SpawnResultHandler.getInstance();
    }

    public static final BuilderCodec<BaxterInteraction> CODEC = BuilderCodec.builder(
            BaxterInteraction.class, BaxterInteraction::new, SimpleInstantInteraction.CODEC
    ).build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler handler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        Ref<EntityStore> playerRef = context.getEntity();
        Player player = commandBuffer.getComponent(playerRef, Player.getComponentType());
        Store<EntityStore> store = player.getWorld().getEntityStore().getStore();

        player.sendMessage(Message.raw("Spawning Baxter!"));
        TransformComponent playerTransform = commandBuffer.getComponent(playerRef, TransformComponent.getComponentType());
        UUIDComponent playerUUID = commandBuffer.getComponent(playerRef, UUIDComponent.getComponentType());

        if (player == null || playerTransform == null) {
            player.sendMessage(Message.raw("The player was not found!"));
            return;
        }
        try {
            spawnBaxter(commandBuffer, playerTransform.getPosition(), playerUUID.getUuid(), player);
        } catch (Exception e) {
            resultHandler.handleError(player, e);
        }
    }

    private void spawnBaxter(CommandBuffer<EntityStore> commandBuffer, Vector3d pos, UUID ownerUUID, Player player) {
        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        holder.addComponent(NPCEntity.getComponentType(), new NPCEntity());
        holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(pos.clone().add(0, 1.0f, 0), new Vector3f()));
        holder.addComponent(Velocity.getComponentType(), new Velocity(new Vector3d(0, 0, 0)));

        holder.ensureComponent(Interactable.getComponentType());

        holder.addComponent(UUIDComponent.getComponentType(), new UUIDComponent(UUID.randomUUID()));
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(commandBuffer.getExternalData().takeNextNetworkId()));

        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Baxter");

        if (modelAsset == null) modelAsset = ModelAsset.DEBUG;
        Model model = Model.createScaledModel(modelAsset, 1.0f);
        holder.addComponent(PersistentModel.getComponentType(), new PersistentModel(model.toReference()));
        holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));

        if (model.getBoundingBox() != null) {
            holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(model.getBoundingBox()));
        }

        holder.addComponent(BaxterComponent.getComponentType(), new BaxterComponent(ownerUUID));

        commandBuffer.addEntity(holder, AddReason.SPAWN);

        resultHandler.handleSuccess(player, holder, "Baxter");
    }
}