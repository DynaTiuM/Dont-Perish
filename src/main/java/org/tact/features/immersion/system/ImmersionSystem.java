package org.tact.features.immersion.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.common.ui.HudManager;
import org.tact.features.comfort.component.ComfortComponent;
import org.tact.features.immersion.ui.ImmersionHud;
import org.tact.features.temperature.component.TemperatureComponent;

public class ImmersionSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(
            float deltaTime,
            int index,
            @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        Ref<EntityStore> entityRef = player.getReference();
        TemperatureComponent temperatureComponent = store.getComponent(entityRef, TemperatureComponent.TYPE);
        ComfortComponent comfortComponent = store.getComponent(entityRef, ComfortComponent.TYPE);

        boolean showFrost;
        boolean showHeat;
        boolean showDiscomfort;

        if (temperatureComponent != null) {
            showFrost = temperatureComponent.isFreezing();
            showHeat = temperatureComponent.isOverheating();
            showDiscomfort = comfortComponent.isUncomfortable();
        } else {
            showDiscomfort = false;
            showHeat = false;
            showFrost = false;
        }

        HudManager.updateChild(player, "immersion", ImmersionHud.class, (hud, builder) -> {
            hud.render(builder, showFrost, showHeat, showDiscomfort);
        });
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }
}