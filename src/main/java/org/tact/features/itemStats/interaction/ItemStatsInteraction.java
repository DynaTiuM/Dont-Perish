package org.tact.features.itemStats.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction; //
import javax.annotation.Nonnull;

public class ItemStatsInteraction extends SimpleInstantInteraction {

    public ItemStatsInteraction() {
        super();
    }

    public static final BuilderCodec<ItemStatsInteraction> CODEC = BuilderCodec.builder(
            ItemStatsInteraction.class,
            ItemStatsInteraction::new,
            SimpleInstantInteraction.CODEC
    ).build();

    @Override
    protected void firstRun(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nonnull CooldownHandler cooldownHandler) {
        System.out.println("✅ Effet du ventilateur appliqué (Stats modifiées) !");
    }
}