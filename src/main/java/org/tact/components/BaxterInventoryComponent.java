package org.tact.components;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer; // L'import correct
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.DontPerish;

public class BaxterInventoryComponent implements Component<EntityStore> {

    private final SimpleItemContainer inventory;

    public BaxterInventoryComponent() {
        this.inventory = new SimpleItemContainer((short) 27);
    }

    public BaxterInventoryComponent(SimpleItemContainer inventory) {
        this.inventory = new SimpleItemContainer(inventory);
    }

    public SimpleItemContainer getInventory() {
        return inventory;
    }

    @Override
    public Component<EntityStore> clone() {
        return new BaxterInventoryComponent(this.inventory);
    }

    public static ComponentType<EntityStore, BaxterInventoryComponent> getComponentType() {
        return DontPerish.baxterInventoryComponent;
    }
}