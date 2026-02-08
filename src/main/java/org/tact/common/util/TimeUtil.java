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

    public static float getSeasonalDayCycleFactor(
            float preciseHour,
            float dayLengthMultiplier
    ) {
        final float ZENITH = 12.0f;
        final float BASE_DAY_HOURS = 14.4f;

        float currentDayDuration = BASE_DAY_HOURS * dayLengthMultiplier;
        currentDayDuration = Math.min(currentDayDuration, 24.0f);
        float distToSunset = currentDayDuration / 2.0f;

        float distFromNoon = Math.abs(preciseHour - ZENITH);

        double angle;

        if (distFromNoon <= distToSunset) {
            angle = (distFromNoon / distToSunset) * (Math.PI / 2.0);
        } else {
            float distInNight = distFromNoon - distToSunset;
            float totalNightDist = 12.0f - distToSunset;

            angle = (Math.PI / 2.0) + ((distInNight / totalNightDist) * (Math.PI / 2.0));
        }

        return (float) Math.cos(angle);
    }
}
