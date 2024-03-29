package com.danais.utils;

import java.util.Calendar;

import java.util.Date;

import java.util.TimeZone;

 

 

/**

 * A set of Calendar utility methods

 */

public class CalendarUtils {

               

                public static final long  SECOND_FACTOR       = 1000;

                public static final long  MINUTE_FACTOR       = 60*SECOND_FACTOR;

                public static final long  HOUR_FACTOR         = 60*MINUTE_FACTOR;

                public static final long  DAY_FACTOR          = 24*HOUR_FACTOR;

 

 

 

    /**

     * Get the offset between GMT and the local timezone

     * @return the offset

     */

    public static long getDefaultTimeZoneOffset() {

        long offset = 0;

        TimeZone zn = TimeZone.getDefault();

        Calendar local = Calendar.getInstance();

        local.setTime(new Date(System.currentTimeMillis()));

 

        // the offset to add to GMT to get local time, modified in case of

        // daylight savings

        int time = (int)(local.get(Calendar.HOUR_OF_DAY)*HOUR_FACTOR +

                         local.get(Calendar.MINUTE)*MINUTE_FACTOR  +

                         local.get(Calendar.SECOND)*SECOND_FACTOR);

        offset = zn.getOffset(1, // era AD

                              local.get(Calendar.YEAR),

                              local.get(Calendar.MONTH),

                              local.get(Calendar.DAY_OF_MONTH),

                              local.get(Calendar.DAY_OF_WEEK),

                              time);

        return offset;

    }

 

   

    /**

     * Shift the time from UTC to the local timezone

     * @param time

     * @return

     */

    public static long adjustTimeToDefaultTimezone(long time) {

                return time+getDefaultTimeZoneOffset();

    }

   

    /**

     * Shift the time from the local timezone do UTC

     * @param time

     * @return

     */

    public static long adjustTimeFromDefaultTimezone(long time) {

                return time-getDefaultTimeZoneOffset();

    }

 

   

}
