package org.tact.core.systems.aura;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class AuraRegistry {
    private static final Logger LOGGER = Logger.getLogger(AuraRegistry.class.getName());

    private final Map<String, AuraHandler> handlers = new LinkedHashMap<>();

    private final Map<Ref<EntityStore>, List<AuraEvent>> activeAuras = new ConcurrentHashMap<>();

    public void register(String id, AuraHandler handler) {
        if (handlers.containsKey(id)) {
            LOGGER.warning("[Aura] Handler '" + id + "' is already registered!");
            return;
        }
        handlers.put(id, handler);
        LOGGER.info("[Aura] Handler registered: " + id);
    }

    public AuraHandler getHandler(String id) {
        return handlers.get(id);
    }

    public Map<String, AuraHandler> getAllHandlers() {
        return Collections.unmodifiableMap(handlers);
    }


    public void setAuras(Ref<EntityStore> source, List<AuraEvent> auras) {
        if (auras.isEmpty()) {
            activeAuras.remove(source);
        } else {
            activeAuras.put(source, auras);
        }
    }

    public List<AuraEvent> getAuras(Ref<EntityStore> source) {
        return activeAuras.getOrDefault(source, Collections.emptyList());
    }

    public Map<Ref<EntityStore>, List<AuraEvent>> getAllAuras() {
        return activeAuras;
    }

    public void removeAuras(Ref<EntityStore> source) {
        activeAuras.remove(source);
    }

    public void clearAuras() {
        activeAuras.clear();
    }

    public void clearHandlers() {
        handlers.clear();
    }

    public void clearAll() {
        clearHandlers();
        clearAuras();
    }
}