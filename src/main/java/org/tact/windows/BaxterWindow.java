package org.tact.windows;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ContainerWindow;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class BaxterWindow extends ContainerWindow {
    public BaxterWindow(@Nonnull ItemContainer itemContainer, Ref<EntityStore> baxterRef, Store<EntityStore> store) {
        super(itemContainer);
        this.registerCloseEvent(event -> {

            if (baxterRef != null && baxterRef.isValid()) {
                AnimationUtils.playAnimation(baxterRef, AnimationSlot.Action, "CloseBaxter", store);

                System.out.println("DEBUG: Fermeture du coffre Baxter détectée !");
            }
        });
    }
}
