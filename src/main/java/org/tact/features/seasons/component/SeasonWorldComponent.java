package org.tact.features.seasons.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.features.seasons.model.Season;

public class SeasonWorldComponent implements Component<EntityStore> {
    private Season currentSeason;
    private float seasonProgress;
    private float seasonTimer;

    public static ComponentType<EntityStore, SeasonWorldComponent> TYPE;

    public static final BuilderCodec<SeasonWorldComponent> CODEC;

    static {
        BuilderCodec.Builder<SeasonWorldComponent> builder = BuilderCodec.builder(
                SeasonWorldComponent.class,
                SeasonWorldComponent::new
        );

        builder.addField(new KeyedCodec<>("CurrentSeason", Codec.STRING),
                (component, value) -> component.setCurrentSeason(Season.valueOf(value)),
                component -> component.getCurrentSeason().name()
        );

        builder.addField(new KeyedCodec<>("SeasonTimer", Codec.FLOAT),
                SeasonWorldComponent::setSeasonTimer,
                SeasonWorldComponent::getSeasonTimer
        );

        builder.addField(new KeyedCodec<>("SeasonProgress", Codec.FLOAT),
                (component, value) -> {
                    if (value != null) component.setSeasonProgress(value);
                },
                SeasonWorldComponent::getSeasonProgress
        );

        CODEC = builder.build();
    }


    public SeasonWorldComponent() {
        this.currentSeason = Season.SPRING;
        this.seasonProgress = 0.0f;
        this.seasonTimer = 0.0f;
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }
    public void setCurrentSeason(Season season) {
        this.currentSeason = season;
    }

    public float getSeasonProgress() {
        return seasonProgress;
    }
    public void setSeasonProgress(float progress) {
        this.seasonProgress = progress;
    }

    public float getSeasonTimer() {
        return seasonTimer;
    }
    public void setSeasonTimer(float timer) {
        this.seasonTimer = timer;
    }

    public void addSeasonTimer(float delta) {
        this.seasonTimer += delta;
    }

    public void resetSeasonTime() {
        this.seasonTimer = 0.0F;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        SeasonWorldComponent cloned = new SeasonWorldComponent();
        cloned.currentSeason = this.currentSeason;
        cloned.seasonProgress = this.seasonProgress;
        cloned.seasonTimer = this.seasonTimer;
        return cloned;
    }

    public static ComponentType<EntityStore, SeasonWorldComponent> getComponentType() {
        return TYPE;
    }
}
