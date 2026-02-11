package org.tact.features.music;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import org.tact.api.Feature;
import org.tact.features.music.component.MusicComponent;

public class MusicFeature implements Feature {

    public MusicFeature() {
    }

    @Override
    public String getId() {
        return "music";
    }

    @Override
    public void registerComponents(JavaPlugin plugin) {
        MusicComponent.TYPE = plugin.getEntityStoreRegistry().registerComponent(
                MusicComponent.class,
                MusicComponent::new
        );
    }

    @Override
    public void registerSystems(JavaPlugin plugin) {
    }

    @Override
    public void registerEvents(JavaPlugin plugin) {
    }

    @Override
    public void enable(JavaPlugin plugin) {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}