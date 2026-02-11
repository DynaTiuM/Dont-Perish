package org.tact.features.itemStats.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.core.systems.aura.AuraEvent;
import org.tact.core.systems.aura.component.AuraComponent;
import org.tact.features.itemStats.config.ItemStats;
import org.tact.features.itemStats.config.ItemStatsConfig;
import org.tact.features.itemStats.util.ItemStatCalculator;

public class ItemAuraSystem extends EntityTickingSystem<EntityStore> {
    private final ItemStatsConfig config;
    private final ComponentType<EntityStore, InteractionManager> managerType;

    public ItemAuraSystem(ItemStatsConfig config) {
        this.config = config;
        this.managerType = InteractionModule.get().getInteractionManagerComponent();
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
        AuraComponent auraComp = archetypeChunk.getComponent(index, AuraComponent.getComponentType());
        InteractionManager interactionManager = archetypeChunk.getComponent(index, managerType);
        Ref<EntityStore> playerRef = archetypeChunk.getReferenceTo(index);

        ItemStack stack = player.getInventory().getItemInHand();

        AuraEvent nextAura = null;

        if (stack != null && !stack.isEmpty()) {
            ItemStats stats = config.getStats(stack.getItemId());

            if (stats != null && stats.auraType != null) {
                boolean shouldEmit = true;

                if (stats.requireUsage) {
                    shouldEmit = ItemStatCalculator.isUsingItem(interactionManager);
                }

                if (shouldEmit) {
                    nextAura = new AuraEvent(
                            stats.auraType,
                            stats.auraStrength,
                            stats.auraRadius,
                            playerRef
                    );
                }
            }
        }
        auraComp.setAura(nextAura);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
                Player.getComponentType(),
                AuraComponent.getComponentType(),
                managerType
        );
    }
}
