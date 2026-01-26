package org.tact.features.baxter.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ContainerWindow;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.tact.features.baxter.component.BaxterComponent;
import org.tact.features.baxter.component.BaxterInventoryComponent;
import org.tact.features.baxter.window.BaxterWindow;

public class BaxterOpenInteraction extends SimpleInstantInteraction {

    public static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD = Message.translation("server.commands.errors.playerNotInWorld");

    public BaxterOpenInteraction() {}

    public static final BuilderCodec<BaxterOpenInteraction> CODEC = BuilderCodec.builder(
            BaxterOpenInteraction.class, BaxterOpenInteraction::new, SimpleInstantInteraction.CODEC
    ).build();

    @Override
    protected void firstRun(
            @NonNullDecl InteractionType type,
            @NonNullDecl InteractionContext context,
            @NonNullDecl CooldownHandler handler
    ) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        Ref<EntityStore> playerRef = context.getEntity();
        Ref<EntityStore> baxterRef = context.getTargetEntity();

        Player player = commandBuffer.getComponent(playerRef, Player.getComponentType());
        BaxterComponent baxterComp = commandBuffer.getComponent(baxterRef, BaxterComponent.getComponentType());
        UUIDComponent playerUUID = commandBuffer.getComponent(playerRef, UUIDComponent.getComponentType());

        BaxterInventoryComponent invComp = commandBuffer.getComponent(baxterRef, BaxterInventoryComponent.getComponentType());

        if (player == null || baxterComp == null) {
            return;
        }

        if (baxterComp.getOwnerUUID().equals(playerUUID.getUuid())) {
            if (invComp != null) {
                openBaxter(player, baxterRef, invComp);
            }
        } else {
            player.sendMessage(Message.raw("This chest belongs to someone else!"));
        }
    }

    private void openBaxter(Player player, Ref<EntityStore> baxterRef, BaxterInventoryComponent invComp) {
        if (baxterRef != null && baxterRef.isValid()) {
            Store<EntityStore> targetStore = baxterRef.getStore();
            World targetWorld = (targetStore.getExternalData()).getWorld();

            targetWorld.execute(() -> {

                SimpleItemContainer baxterInventory = invComp.getInventory();

                ContainerWindow containerWindow = new BaxterWindow(baxterInventory, baxterRef, targetStore);

                player.getPageManager().setPageWithWindows(
                        player.getReference(),
                        player.getReference().getStore(),
                        Page.Bench,
                        true,
                        containerWindow
                );
                AnimationUtils.playAnimation(baxterRef, AnimationSlot.Action, "OpenBaxter", targetStore);
            });
        } else {
            player.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
        }
    }
}