package org.tact.features.baxter.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class BaxterConfig {

    public static final BuilderCodec<BaxterConfig> CODEC;
    public float movementSpeed = 5.0F;
    public float minFollowDistance = 3F;
    public float flySpeed = 10.0F;
    public float teleportThreshold = 30.0F;
    public boolean enabled = true;

    public int inventorySize = 27;
    public String inventoryTitle = "Baxter's inventory";

    public BaxterConfig() {

    }

    static {
        BuilderCodec.Builder<BaxterConfig> b = BuilderCodec.builder(BaxterConfig.class, BaxterConfig::new);

        b.append(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                (cfg, v) -> cfg.enabled = v,
                cfg -> cfg.enabled);

        b.append(new KeyedCodec<>("MovementSpeed", Codec.FLOAT),
                (cfg, v) -> cfg.movementSpeed = v,
                cfg -> cfg.movementSpeed);

        b.append(new KeyedCodec<>("MinFollowDistance", Codec.FLOAT),
                (cfg, v) -> cfg.minFollowDistance = v,
                cfg -> cfg.minFollowDistance);

        b.append(new KeyedCodec<>("TeleportThreshold", Codec.FLOAT),
                (cfg, v) -> cfg.teleportThreshold = v,
                cfg -> cfg.teleportThreshold);

        b.append(new KeyedCodec<>("flySpeed", Codec.FLOAT),
                (cfg, v) -> cfg.flySpeed = v,
                cfg -> cfg.flySpeed);

        b.append(new KeyedCodec<>("InventorySize", Codec.INTEGER),
                (cfg, v) -> cfg.inventorySize = v,
                cfg -> cfg.inventorySize);

        b.append(new KeyedCodec<>("InventoryTitle", Codec.STRING),
                (cfg, v) -> cfg.inventoryTitle = v,
                cfg -> cfg.inventoryTitle);

        CODEC = b.build();
    }
}
