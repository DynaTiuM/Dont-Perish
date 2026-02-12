package org.tact.common.util;

import com.hypixel.hytale.builtin.weather.resources.WeatherResource;
import com.hypixel.hytale.server.core.asset.type.weather.config.Weather;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;

import static com.hypixel.hytale.math.util.ChunkUtil.indexChunkFromBlock;

public class WeatherHelper {
    private static int getPlayerEnvironmentId(
            Player player,
            TransformComponent transformComponent
    ) {
        World world = player.getWorld();
        if (world == null) return 0;

        if (transformComponent == null) return 0;

        var pos = transformComponent.getPosition();
        int blockX = (int) Math.floor(pos.x);
        int blockY = (int) Math.floor(pos.y);
        int blockZ = (int) Math.floor(pos.z);

        long chunkIndex = indexChunkFromBlock(blockX, blockZ);

        WorldChunk chunk = world.getChunk(chunkIndex);
        if (chunk == null) return 0;

        var blockChunk = chunk.getBlockChunk();
        if (blockChunk == null) return 0;

        var envChunk = blockChunk.getEnvironmentChunk();
        if (envChunk == null) return 0;

        return envChunk.get(blockX & 31, blockY, blockZ & 31);
    }

    public static String getWeatherId(
            Player player,
            WeatherResource weatherResource,
            TransformComponent transformComponent
    ) {
        int weatherIdx = weatherResource.getForcedWeatherIndex();

        if (weatherIdx == 0) {
            int environmentId = getPlayerEnvironmentId(player, transformComponent);

            weatherIdx = weatherResource.getWeatherIndexForEnvironment(environmentId);
        }

        if (weatherIdx > 0) {
            var weatherAsset = Weather.getAssetMap().getAsset(weatherIdx);
            if (weatherAsset != null) {
                return weatherAsset.getId();
            }
        }
        return null;
    }
}