package org.tact.api;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;

public interface Feature {
    /**
     * Unique Identifier or the Feature
     */
    String getId();

    /**
     * Register the Components
     */
    void registerComponents(JavaPlugin plugin);

    /**
     * Register the Systems
     */
    void registerSystems(JavaPlugin plugin);

    /**
     * Register the Events
     */
    void registerEvents(JavaPlugin plugin);

    /**
     * Enables the Feature (at the start)
     */
    void enable(JavaPlugin plugin);

    /**
     * Verifies whether the feature is enabled in config
     */
    boolean isEnabled();
}