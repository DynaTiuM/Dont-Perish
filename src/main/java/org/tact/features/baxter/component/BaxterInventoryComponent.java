package org.tact.features.baxter.component;


import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class BaxterInventoryComponent implements Component<EntityStore> {

    private final SimpleItemContainer inventory;
    public static ComponentType<EntityStore, BaxterInventoryComponent> TYPE;

    public BaxterInventoryComponent(int size) {
        this.inventory = new SimpleItemContainer((short) size);
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
        return TYPE;
    }
}