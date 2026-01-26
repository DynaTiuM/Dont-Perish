package org.tact;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.commands.BaxterCommand;
import org.tact.components.BaxterComponent;

import org.tact.components.BaxterInventoryComponent;
import org.tact.interactions.BaxterOpenInteraction;
import org.tact.systems.BaxterSystem;

import javax.annotation.Nonnull;

public class DontPerish extends JavaPlugin {
    public DontPerish(@Nonnull JavaPluginInit init) {
        super(init);
    }
    public static ComponentType<EntityStore, BaxterComponent> baxterComponent;
    public static ComponentType<EntityStore, BaxterInventoryComponent> baxterInventoryComponent;

    @Override
    protected void setup() {
        baxterComponent = this.getEntityStoreRegistry().registerComponent(BaxterComponent.class, BaxterComponent::new);
        baxterInventoryComponent = this.getEntityStoreRegistry().registerComponent(BaxterInventoryComponent.class, BaxterInventoryComponent::new);

        this.getCodecRegistry(Interaction.CODEC).register("OpenBaxter", BaxterOpenInteraction.class, BaxterOpenInteraction.CODEC);
        this.getEntityStoreRegistry().registerSystem(new BaxterSystem());
        this.getCommandRegistry().registerCommand(new BaxterCommand());

    }
}