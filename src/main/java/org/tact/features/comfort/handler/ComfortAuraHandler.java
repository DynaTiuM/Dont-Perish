package org.tact.features.comfort.handler;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.common.aura.AuraEvent;
import org.tact.common.aura.AuraHandler;
import org.tact.common.util.StatHelper;
import org.tact.features.comfort.config.ComfortConfig;

import java.util.List;

public class ComfortAuraHandler implements AuraHandler {

    private final ComfortConfig config;
    private int comfortStatIndex = -1;

    public ComfortAuraHandler(ComfortConfig config) {
        this.config = config;
    }

    @Override
    public void onAurasDetected(
            Player player,
            Ref<EntityStore> entityRef,
            Store<EntityStore> store,
            List<AuraEvent> nearbyAuras,
            float deltaTime
    ) {
        EntityStatMap statMap = store.getComponent(entityRef, EntityStatMap.getComponentType());
        if (statMap == null) return;

        EntityStatValue comfortStat = statMap.get(getComfortStatIndex());
        if (comfortStat == null) return;

        float currentComfort = comfortStat.get();
        float comfortGain = getComfortGain(nearbyAuras);

        if (comfortGain > 0.0f) {
            float newComfort = currentComfort + comfortGain * deltaTime;
            newComfort = StatHelper.clamp(comfortStat, newComfort);
            statMap.setStatValue(getComfortStatIndex(), newComfort);
        }
    }

    private float getComfortGain(List<AuraEvent> nearbyAuras) {
        float comfortGain = 0.0F;

        for (AuraEvent aura : nearbyAuras) {
            switch (aura.getType()) {
                case "music":
                    comfortGain += aura.getStrength() * config.musicComfortBonus;
                    break;
                case "speech":
                    comfortGain += aura.getStrength() * config.speechComfortBonus;
                    break;
                case "laughter":
                    comfortGain += aura.getStrength() * config.laughterComfortBonus;
                    break;
            }
        }
        return comfortGain;
    }

    private int getComfortStatIndex() {
        if (comfortStatIndex == -1) {
            comfortStatIndex = EntityStatType.getAssetMap().getIndex("Comfort");
        }
        return comfortStatIndex;
    }
}