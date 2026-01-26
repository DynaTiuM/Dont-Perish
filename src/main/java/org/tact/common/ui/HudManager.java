package org.tact.common.ui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.tact.common.utils.ReflectionHelper;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public class HudManager {
    private static final String MHUD_CLASS = "com.buuz135.mhud.MultipleHUD";
    private static final String MHUD_CONTAINER_CLASS = "com.buuz135.mhud.MultipleCustomUIHud";

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

    public static <T extends CustomUIHud> void ifPresent(
            @Nonnull Player player,
            @Nonnull String hudIdentifier,
            @Nonnull Class<T> hudClass,
            @Nonnull Consumer<T> consumer
    ) {
        T hud = findHud(player, hudIdentifier, hudClass);
        if (hud != null) {
            consumer.accept(hud);
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

    private static void openWithInternalSystem(
            Player player,
            PlayerRef playerRef,
            CustomUIHud newHud,
            String hudIdentifier
    ) {
        com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager hudManager =
                player.getHudManager();
        CustomUIHud existing = hudManager.getCustomHud();

        Map<String, CustomUIHud> allHuds = new LinkedHashMap<>();
        unpackHuds(existing, allHuds);
        allHuds.put(hudIdentifier, newHud);

        CustomUIHud finalHud = buildFinalHud(playerRef, allHuds);
        hudManager.setCustomHud(playerRef, finalHud);
    }

    private static CustomUIHud buildFinalHud(
            PlayerRef playerRef,
            Map<String, CustomUIHud> huds
    ) {
        if (huds.isEmpty()) {
            return null;
        }

        if (huds.size() == 1) {
            return huds.values().iterator().next();
        }

        CustomUIHud mhudContainer = tryCreateMhudContainer(playerRef, huds);
        if (mhudContainer != null) {
            return mhudContainer;
        }

        return new MultiHudContainer(playerRef, huds);
    }

    private static void unpackHuds(CustomUIHud hud, Map<String, CustomUIHud> collector) {
        if (hud == null) {
            return;
        }

        if (isHudContainer(hud)) {
            Map<String, CustomUIHud> contained = extractContainedHuds(hud);
            if (contained != null) {
                collector.putAll(contained);
                return;
            }
        }

        collector.put(hud.getClass().getName(), hud);
    }

    private static boolean isHudContainer(CustomUIHud hud) {
        String className = hud.getClass().getName();
        return hud instanceof MultiHudContainer ||
                className.equals(MHUD_CONTAINER_CLASS);
    }

    private static Map<String, CustomUIHud> extractContainedHuds(CustomUIHud container) {
        try {
            Object result = ReflectionHelper.invokeMethod(container, "getCustomHuds");
            if (result instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<?, ?> rawMap = (Map<?, ?>) result;
                Map<String, CustomUIHud> huds = new LinkedHashMap<>();

                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    if (entry.getValue() instanceof CustomUIHud) {
                        huds.put(entry.getKey().toString(), (CustomUIHud) entry.getValue());
                    }
                }
                return huds;
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static <T extends CustomUIHud> T findHud(
            Player player,
            String hudIdentifier,
            Class<T> hudClass
    ) {
        CustomUIHud current = player.getHudManager().getCustomHud();

        T found = findInMhud(current, hudIdentifier, hudClass);
        if (found != null) {
            return found;
        }

        return findRecursive(current, hudClass);
    }

    private static <T extends CustomUIHud> T findInMhud(
            CustomUIHud current,
            String hudIdentifier,
            Class<T> targetClass
    ) {
        if (current == null || !current.getClass().getName().equals(MHUD_CONTAINER_CLASS)) {
            return null;
        }

        try {
            Method getHuds = current.getClass().getMethod("getCustomHuds");
            @SuppressWarnings("unchecked")
            Map<String, CustomUIHud> huds = (Map<String, CustomUIHud>) getHuds.invoke(current);

            CustomUIHud targeted = huds.get(hudIdentifier);
            if (targetClass.isInstance(targeted)) {
                return targetClass.cast(targeted);
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static <T extends CustomUIHud> T findRecursive(
            CustomUIHud current,
            Class<T> targetClass
    ) {
        if (current == null) {
            return null;
        }

        if (targetClass.isInstance(current)) {
            return targetClass.cast(current);
        }

        Collection<CustomUIHud> children = getChildHuds(current);
        for (CustomUIHud child : children) {
            T found = findRecursive(child, targetClass);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    private static Collection<CustomUIHud> getChildHuds(CustomUIHud hud) {
        List<CustomUIHud> children = new ArrayList<>();

        if (hud instanceof MultiHudContainer) {
            children.addAll(((MultiHudContainer) hud).getChildren().values());
        }

        try {
            Object result = ReflectionHelper.invokeMethod(hud, "getCustomHuds");
            if (result instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) result;
                for (Object value : map.values()) {
                    if (value instanceof CustomUIHud) {
                        children.add((CustomUIHud) value);
                    }
                }
            } else if (result instanceof Collection) {
                Collection<?> col = (Collection<?>) result;
                for (Object value : col) {
                    if (value instanceof CustomUIHud) {
                        children.add((CustomUIHud) value);
                    }
                }
            }
        } catch (Exception e) {
        }

        return children;
    }

    private static CustomUIHud tryCreateMhudContainer(
            PlayerRef playerRef,
            Map<String, CustomUIHud> huds
    ) {
        try {
            Class<?> clazz = Class.forName(MHUD_CONTAINER_CLASS);
            Constructor<?> constructor = clazz.getConstructor(
                    PlayerRef.class,
                    HashMap.class
            );
            return (CustomUIHud) constructor.newInstance(
                    playerRef,
                    new HashMap<>(huds)
            );
        } catch (Exception e) {
            return null;
        }
    }

    private static class MultiHudContainer extends CustomUIHud {
        private final Map<String, CustomUIHud> children;

        public MultiHudContainer(PlayerRef playerRef, Map<String, CustomUIHud> children) {
            super(playerRef);
            this.children = new LinkedHashMap<>(children);
        }

        @Override
        protected void build(@Nonnull UICommandBuilder builder) {
            for (CustomUIHud child : children.values()) {
                if (child != null) {
                    ReflectionHelper.invokeMethod(child, "build", builder);
                }
            }
        }

        public Map<String, CustomUIHud> getChildren() {
            return Collections.unmodifiableMap(children);
        }
    }
}