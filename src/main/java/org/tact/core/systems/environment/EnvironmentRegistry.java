package org.tact.core.systems.environment;

import java.util.*;
import java.util.logging.Logger;

public class EnvironmentRegistry {
    private static final Logger LOGGER = Logger.getLogger(EnvironmentRegistry.class.getName());
    private final Map<String, EnvironmentHandler> handlers = new LinkedHashMap<>();

    public void register(String id, EnvironmentHandler handler) {
        if (handlers.containsKey(id)) {
            LOGGER.warning("[Environment] Handler " + id + "is already registered!");
            return;
        }
        handlers.put(id, handler);

        LOGGER.info("[Environment] Handler registered: " + id);
    }

    public EnvironmentHandler getHandler(String id) {
        return handlers.get(id);
    }

    public Map<String, EnvironmentHandler> getAllHandlers() {
        return Collections.unmodifiableMap(handlers);
    }

    public void clear() {
        handlers.clear();
    }
}
