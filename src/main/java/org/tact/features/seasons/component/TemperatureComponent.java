package org.tact.features.seasons.component;

<<<<<<< HEAD
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
=======
import com.hypixel.hytale.component.Component;
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class TemperatureComponent implements Component<EntityStore> {
<<<<<<< HEAD
    private float currentTemperature = 20.0f;
    private float targetTemperature = 20.0f;
    private boolean hasProtection = false;
    private float damageTimer = 0.0f;

    public static ComponentType<EntityStore, TemperatureComponent> TYPE;

    public static final BuilderCodec<TemperatureComponent> CODEC;

    static {
        BuilderCodec.Builder<TemperatureComponent> builder = BuilderCodec.builder(
            TemperatureComponent.class,
            TemperatureComponent::new
        );

        builder.addField(new KeyedCodec<>("CurrentTemperature", Codec.FLOAT),
                TemperatureComponent::setCurrentTemperature,
                TemperatureComponent::getCurrentTemperature
        );
        builder.addField(new KeyedCodec<>("DamageTimer", Codec.FLOAT),
                (comp, value) -> comp.damageTimer = value,
                TemperatureComponent::getDamageTimer
        );

        CODEC = builder.build();
    }

    public TemperatureComponent() { }

=======
    private float currentTemperature;
    private float targetTemperature;
    private boolean hasProtection;
    private float damageTimer;

    public TemperatureComponent() {
        this.currentTemperature = 20.0f;
        this.targetTemperature = 20.0f;
        this.hasProtection = false;
        this.damageTimer = 0.0f;
    }

>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
    public float getCurrentTemperature() {
        return this.currentTemperature;
    }
    public void setCurrentTemperature(float temperature) {
        this.currentTemperature = temperature;
    }

    public float getTargetTemperature() {
        return targetTemperature;
    }
    public void setTargetTemperature(float temperature) {
        this.targetTemperature = temperature;
    }

    public boolean hasProtection() {
        return this.hasProtection;
    }
    public void setHasProtection(boolean hasProtection) {
        this.hasProtection = hasProtection;
    }

    public float getDamageTimer() {
        return this.damageTimer;
    }
    public void addDamageTimer(float delta) {
        this.damageTimer += delta;
    }
    public void resetDamageTimer() {
        this.damageTimer = 0.0f;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        TemperatureComponent cloned = new TemperatureComponent();
        cloned.currentTemperature = this.currentTemperature;
        cloned.targetTemperature = this.targetTemperature;
        cloned.hasProtection = this.hasProtection;
        cloned.damageTimer = this.damageTimer;
        return cloned;
    }
<<<<<<< HEAD

    public static ComponentType<EntityStore, TemperatureComponent> getComponentType() {
        return TemperatureComponent.TYPE;
    }
=======
>>>>>>> 5d3194d (feat: seasons, World Ref still not found)
}
