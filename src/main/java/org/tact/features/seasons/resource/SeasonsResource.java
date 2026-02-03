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

    public static final BuilderCodec<SeasonsResource> CODEC;

    static {
        BuilderCodec.Builder<SeasonsResource> builder = BuilderCodec.builder(
                SeasonsResource.class,
                SeasonsResource::new
        );

        builder.addField(new KeyedCodec<>("CurrentSeason", Codec.STRING),
                (resource, value) ->
                    resource.setCurrentSeason(Season.valueOf(value)),
                resource -> {
                    String season = resource.getCurrentSeason().name();
                    System.out.println("[Seasons] SAVING Season: " + season);
                    return season;
                }
        );

        builder.addField(new KeyedCodec<>("SeasonTimer", Codec.FLOAT),
                SeasonsResource::setSeasonTimer,
                resource -> {
                    float timer = resource.getSeasonTimer();
                    System.out.println("[Seasons] SAVING Timer: " + timer + "s");
                    return timer;
                }
        );

        builder.addField(new KeyedCodec<>("SeasonProgress", Codec.FLOAT),
                (resource, value) -> {
                    if (value != null) {
                        resource.setSeasonProgress(value);
                        System.out.println("[Seasons] Codec -> Loaded Progress: " + value);
                    }
                },
                resource -> {
                    float progress = resource.getSeasonProgress();
                    System.out.println("[Seasons] SAVING Progress: " + progress);
                    return progress;
                }
        );

        CODEC = builder.build();
    }

    public Season getCurrentSeason() { return currentSeason; }
    public void setCurrentSeason(Season season) { this.currentSeason = season; }
    public float getSeasonTimer() { return seasonTimer; }
    public void setSeasonTimer(float timer) { this.seasonTimer = timer; }
    public void addSeasonTimer(float delta) { this.seasonTimer += delta; }
    public float getSeasonProgress() { return seasonProgress; }
    public void setSeasonProgress(float progress) { this.seasonProgress = progress; }

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
        return cloned;
    }
}
