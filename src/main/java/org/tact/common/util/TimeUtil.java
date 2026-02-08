package org.tact.common.util;

import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;

import java.time.LocalDateTime;

public class TimeUtil {

    public static float getPreciseHour(WorldTimeResource timeResource) {
        if(timeResource == null) return 0.0F;

        LocalDateTime gameDateTime = timeResource.getGameDateTime();
        int hour = gameDateTime.getHour();
        int minute = gameDateTime.getMinute();

        return hour + (minute / 60.0F);
    }

    public static float getSeasonalDayCycleFactor(float preciseHour, float seasonStretch) {
        float zenith = 14.0f;
        double angle = ((preciseHour - zenith) / (24.0f * seasonStretch)) * 2.0f * Math.PI;

        return (float) Math.cos(angle);
    }
}
