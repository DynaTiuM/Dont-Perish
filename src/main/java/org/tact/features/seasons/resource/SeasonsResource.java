package org.tact.features.seasons.resource;


import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.tact.features.seasons.model.Season;

public class SeasonsResource implements Resource<EntityStore> {
    public static ResourceType<EntityStore, SeasonsResource> TYPE;

    private Season currentSeason = Season.SPRING;
    private float seasonTimer = 0.0f;
    private float seasonProgress = 0.0f;

    private boolean isWeatherForced = false;
    private float weatherForcedDuration = 0.0f;
    private float weatherCheckTimer = 0.0f;

    public static final BuilderCodec<SeasonsResource> CODEC;

    static {
        BuilderCodec.Builder<SeasonsResource> builder = BuilderCodec.builder(
                SeasonsResource.class,
                SeasonsResource::new
        );

        builder.append(new KeyedCodec<>("CurrentSeason", Codec.STRING),
                (resource, value) ->
                    resource.setCurrentSeason(Season.valueOf(value)),
                resource -> {
                    return resource.getCurrentSeason().name();
                }
        ).add();

        builder.append(new KeyedCodec<>("SeasonTimer", Codec.FLOAT),
                SeasonsResource::setSeasonTimer,
                SeasonsResource::getSeasonTimer
        ).add();

        builder.append(new KeyedCodec<>("SeasonProgress", Codec.FLOAT),
                (resource, value) -> {
                    if (value != null) {
                        resource.setSeasonProgress(value);
                    }
                },
                SeasonsResource::getSeasonProgress
        ).add();

        builder.append(new KeyedCodec<>("IsWeatherForced", Codec.BOOLEAN),
                (resource, value) -> {
                    if (value != null) {
                        resource.setWeatherForced(value);
                    }
                },
                SeasonsResource::isWeatherForced
        ).add();

        builder.append(new KeyedCodec<>("WeatherForcedDuration", Codec.FLOAT),
                (resource, value) -> {
                    if (value != null) {
                        resource.setWeatherForcedDuration(value);
                    }
                },
                SeasonsResource::getWeatherForcedDuration
        ).add();

        builder.append(new KeyedCodec<>("WeatherCheckTimer", Codec.FLOAT),
                (resource, value) -> {
                    if (value != null) {
                        resource.setWeatherCheckTimer(value);
                    }
                },
                SeasonsResource::getWeatherCheckTimer
        ).add();

        CODEC = builder.build();
    }

    public Season getCurrentSeason() { return currentSeason; }
    public void setCurrentSeason(Season season) { this.currentSeason = season; }
    public float getSeasonTimer() { return seasonTimer; }
    public void setSeasonTimer(float timer) { this.seasonTimer = timer; }
    public void addSeasonTimer(float delta) { this.seasonTimer += delta; }
    public float getSeasonProgress() { return seasonProgress; }
    public void setSeasonProgress(float progress) { this.seasonProgress = progress; }

    public boolean isWeatherForced() { return isWeatherForced; }
    public void setWeatherForced(boolean forced) { isWeatherForced = forced; }

    public float getWeatherForcedDuration() { return weatherForcedDuration; }
    public void setWeatherForcedDuration(float duration) { weatherForcedDuration = duration; }

    public void decreaseWeatherForcedDuration(float dt) { weatherForcedDuration -= dt; }

    public float getWeatherCheckTimer() { return weatherCheckTimer; }
    public void addWeatherCheckTimer(float dt) { weatherCheckTimer += dt; }
    public void setWeatherCheckTimer(float timer) { weatherCheckTimer = timer; }

    public void resetSeasonTime() {
        this.seasonTimer = 0.0F;
    }

    @NullableDecl
    @Override
    public Resource<EntityStore> clone() {
        SeasonsResource cloned = new SeasonsResource();
        cloned.currentSeason = this.currentSeason;
        cloned.seasonTimer = this.seasonTimer;
        cloned.seasonProgress = this.seasonProgress;

        cloned.isWeatherForced = this.isWeatherForced;
        cloned.weatherCheckTimer = this.weatherCheckTimer;
        cloned.weatherForcedDuration = this.weatherForcedDuration;
        return cloned;
    }
}
