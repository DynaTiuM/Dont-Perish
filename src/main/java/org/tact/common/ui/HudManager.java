package org.tact.common.ui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

public class HudManager {
    private static final String MHUD_CLASS = "com.buuz135.mhud.MultipleHUD";

    public static void open(
            @Nonnull Player player,
            @Nonnull PlayerRef playerRef,
            @Nonnull CustomUIHud newHud,
            @Nonnull String hudIdentifier
    ) {
        if (tryOpenWithMhud(player, playerRef, newHud, hudIdentifier)) {
            return;
        }

        openWithInternalSystem(player, playerRef, newHud, hudIdentifier);
    }

    private static void openWithInternalSystem(
            Player player,
            PlayerRef playerRef,
            CustomUIHud newHud,
            String hudIdentifier
    ) {
        var hudManager = player.getHudManager();
        CustomUIHud current = hudManager.getCustomHud();

        RootHud newRoot = new RootHud(playerRef);

        if (current instanceof RootHud existingRoot) {
            existingRoot.getHuds().forEach(newRoot::register);
        }

        newRoot.register(hudIdentifier, newHud);

        hudManager.setCustomHud(playerRef, newRoot);
    }

    public static <T extends CustomUIHud> void updateChild(
            Player player,
            String id,
            Class<T> clazz,
            java.util.function.BiConsumer<T, UICommandBuilder> updateAction
    ) {
        if (player == null) return;
        var hudManager = player.getHudManager();
        if (hudManager == null) return;

        CustomUIHud current = hudManager.getCustomHud();

        if (current instanceof RootHud root) {
            CustomUIHud child = root.getHuds().get(id);

            if (child != null && clazz.isInstance(child)) {
                try {
                    T typedChild = clazz.cast(child);

                    UICommandBuilder builder = new UICommandBuilder();
                    updateAction.accept(typedChild, builder);

                    root.update(false, builder);
                } catch (Exception e) {
                }
            }
        }
    }

    private static boolean tryOpenWithMhud(
            Player player,
            PlayerRef playerRef,
            CustomUIHud newHud,
            String hudIdentifier
    ) {
        try {
            Class<?> mhudClass = Class.forName(MHUD_CLASS);
            Method getInstance = mhudClass.getMethod("getInstance");
            Object instance = getInstance.invoke(null);

            if (instance != null) {
                Method setHud = mhudClass.getMethod(
                        "setCustomHud",
                        Player.class,
                        PlayerRef.class,
                        String.class,
                        CustomUIHud.class
                );
                setHud.invoke(instance, player, playerRef, hudIdentifier, newHud);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
}