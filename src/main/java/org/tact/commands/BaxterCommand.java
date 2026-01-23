package org.tact.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import org.tact.components.BaxterComponent;
import org.tact.components.BaxterInventoryComponent;
import org.tact.services.SpawnResultHandler;

import javax.annotation.Nonnull;
import java.util.UUID;

public class BaxterCommand extends AbstractPlayerCommand {

    private final SpawnResultHandler spawnResultHandler;

    public BaxterCommand() {
        super("baxter", "Spawn Baxter");
        spawnResultHandler = SpawnResultHandler.getInstance();
    }

    private void spawnBaxter(Store<EntityStore> commandBuffer, com.hypixel.hytale.math.vector.Vector3d pos, UUID ownerUUID) {

        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();

        holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(pos.clone().add(0, 1, 0), new Vector3f()));
        holder.addComponent(Velocity.getComponentType(), new Velocity(new com.hypixel.hytale.math.vector.Vector3d(0, 0, 0)));
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(commandBuffer.getExternalData().takeNextNetworkId()));
        holder.addComponent(UUIDComponent.getComponentType(), new UUIDComponent(UUID.randomUUID()));

        addInteraction(holder);
        addModel(holder);
        addHealth(holder);

        holder.addComponent(BaxterComponent.getComponentType(), new BaxterComponent(ownerUUID));
        holder.addComponent(BaxterInventoryComponent.getComponentType(), new BaxterInventoryComponent());

        holder.addComponent(ActiveAnimationComponent.getComponentType(), new ActiveAnimationComponent());

        holder.ensureComponent(DamageDataComponent.getComponentType());

        commandBuffer.addEntity(holder, com.hypixel.hytale.component.AddReason.SPAWN);
    }

    private void addInteraction(Holder<EntityStore> holder) {
        holder.ensureComponent(Interactable.getComponentType());
        Interactions interactions = new Interactions();
        interactions.setInteractionId(InteractionType.Use, "Root_Baxter_Use");
        interactions.setInteractionHint("Open Baxter");
        holder.addComponent(Interactions.getComponentType(), interactions);
    }

    private void addModel(Holder<EntityStore> holder) {
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Baxter");
        if (modelAsset == null) modelAsset = ModelAsset.DEBUG;
        Model model = Model.createScaledModel(modelAsset, 1.0f);
        holder.addComponent(PersistentModel.getComponentType(), new PersistentModel(model.toReference()));
        holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));

        if (model.getBoundingBox() != null) {
            holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(model.getBoundingBox()));
        }
    }

    private void addHealth(Holder<EntityStore> holder) {
        holder.ensureComponent(EntityStatMap.getComponentType());
        EntityStatMap stats = holder.getComponent(EntityStatMap.getComponentType());
        int healthIndex = DefaultEntityStatTypes.getHealth();

        StaticModifier healthBoost = new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                500.0f
        );

        stats.putModifier(healthIndex, "BaxterGodMode", healthBoost);

        stats.setStatValue(healthIndex, 500.0f);
    }

    @Override
    protected void execute(
            @Nonnull CommandContext commandContext,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        UUIDComponent uuid = store.getComponent(ref, UUIDComponent.getComponentType());

        try {
            spawnBaxter(store, transform.getPosition(), uuid.getUuid());
            spawnResultHandler.handleSuccess(player, uuid, "Baxter");
        } catch (Exception e) {
            spawnResultHandler.handleError(player, e);
        }
    }
}