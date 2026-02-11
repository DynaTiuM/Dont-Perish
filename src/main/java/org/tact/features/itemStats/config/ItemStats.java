package org.tact.features.itemStats.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ItemStats {
    public static final BuilderCodec<ItemStats> CODEC;

    @NullableDecl
    public String auraType = null;
    public float auraRadius = 0.0f;
    public float auraStrength = 0.0f;

    public float thermalOffset = 0.0f;
    public float insulationCooling = 0.0f;
    public float insulationHeating = 0.0f;

    public float comfortModifier = 0.0f;

    public float walkSpeedModifier = 0.0f;
    public float healthChangePerSecond = 0.0f;
    public float staminaDrainPerSecond = 0.0f;

    public boolean activeInHand = false;
    public boolean activeInArmor = false;
    public boolean activeInInventory = false;

    public boolean requireUsage = false;

    public ItemStats() {}

    static {
        BuilderCodec.Builder<ItemStats> b = BuilderCodec.builder(ItemStats.class, ItemStats::new);

        b.append(new KeyedCodec<>("ThermalOffset", Codec.FLOAT), (c, v) -> c.thermalOffset = v, c -> c.thermalOffset).add();
        b.append(new KeyedCodec<>("InsulationCooling", Codec.FLOAT), (c, v) -> c.insulationCooling = v, c -> c.insulationCooling).add();
        b.append(new KeyedCodec<>("InsulationHeating", Codec.FLOAT), (c, v) -> c.insulationHeating = v, c -> c.insulationHeating).add();

        b.append(new KeyedCodec<>("ComfortModifier", Codec.FLOAT), (c, v) -> c.comfortModifier = v, c -> c.comfortModifier).add();

        b.append(new KeyedCodec<>("WalkSpeedModifier", Codec.FLOAT), (c, v) -> c.walkSpeedModifier = v, c -> c.walkSpeedModifier).add();
        b.append(new KeyedCodec<>("HealthChangePerSecond", Codec.FLOAT), (c, v) -> c.healthChangePerSecond = v, c -> c.healthChangePerSecond).add();
        b.append(new KeyedCodec<>("StaminaDrainPerSecond", Codec.FLOAT), (c, v) -> c.staminaDrainPerSecond = v, c -> c.staminaDrainPerSecond).add();

        b.append(new KeyedCodec<>("ActiveInHand", Codec.BOOLEAN), (c, v) -> c.activeInHand = v, c -> c.activeInHand).add();
        b.append(new KeyedCodec<>("ActiveInArmor", Codec.BOOLEAN), (c, v) -> c.activeInArmor = v, c -> c.activeInArmor).add();
        b.append(new KeyedCodec<>("ActiveInInventory", Codec.BOOLEAN), (c, v) -> c.activeInInventory = v, c -> c.activeInInventory).add();

        b.append(new KeyedCodec<>("AuraType", Codec.STRING), (s, v) -> s.auraType = v, s -> s.auraType).add();
        b.append(new KeyedCodec<>("AuraRadius", Codec.FLOAT), (s, v) -> s.auraRadius = v, s -> s.auraRadius).add();
        b.append(new KeyedCodec<>("AuraStrength", Codec.FLOAT), (s, v) -> s.auraStrength = v, s -> s.auraStrength).add();

        b.append(new KeyedCodec<>("RequireUsage", Codec.BOOLEAN), (c, v) -> c.requireUsage = v, c -> c.requireUsage).add();
        
        CODEC = b.build();
    }
}