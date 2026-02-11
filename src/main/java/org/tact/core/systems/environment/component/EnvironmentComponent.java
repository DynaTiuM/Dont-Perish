package org.tact.core.systems.environment.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.tact.core.systems.environment.EnvironmentResult;

public class EnvironmentComponent implements Component<EntityStore> {

    public transient EnvironmentResult lastResult = new EnvironmentResult(0);

    public static ComponentType<EntityStore, EnvironmentComponent> TYPE;

    @Override
    public Component<EntityStore> clone() { return new EnvironmentComponent(); }
    public static ComponentType<EntityStore, EnvironmentComponent> getComponentType() { return TYPE; }
}