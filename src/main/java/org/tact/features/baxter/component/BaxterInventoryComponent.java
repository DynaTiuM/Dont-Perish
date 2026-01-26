package org.tact.features.baxter.component;


import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class BaxterInventoryComponent implements Component<EntityStore> {

    private final SimpleItemContainer inventory;
    public static ComponentType<EntityStore, BaxterInventoryComponent> TYPE;

    public static final BuilderCodec<BaxterInventoryComponent> CODEC;

    static {
        BuilderCodec.Builder<BaxterInventoryComponent> builder = BuilderCodec.builder(
                BaxterInventoryComponent.class,
                () -> new BaxterInventoryComponent(27)
        );

        builder.addField(
                new KeyedCodec<>("Inventory", SimpleItemContainer.CODEC),

                (comp, loadedContainer) -> {
                    comp.getInventory().clear();

                    int safeLimit = Math.min(comp.getInventory().getCapacity(), loadedContainer.getCapacity());

                    for (int i = 0; i < safeLimit; i++) {
                        ItemStack item = loadedContainer.getItemStack((short) i);
                        if (item == null) {
                            item = ItemStack.EMPTY;
                        }

                        comp.getInventory().setItemStackForSlot((short) i, item);
                    }
                },

                (comp) -> comp.inventory
        );

        CODEC = builder.build();
    }

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