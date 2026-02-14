package ique.daitechagent.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private DateUtils() {
    }

    public static String formatTime(long timeInMillis) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Long.valueOf(timeInMillis));
    }

    public static String formatTimeWithMarker(long timeInMillis) {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(Long.valueOf(timeInMillis));
    }

    public static int getHourOfDay(long timeInMillis) {
        return Integer.valueOf(new SimpleDateFormat("H", Locale.getDefault()).format(Long.valueOf(timeInMillis))).intValue();
    }

    public static int getMinute(long timeInMillis) {
        return Integer.valueOf(new SimpleDateFormat("m", Locale.getDefault()).format(Long.valueOf(timeInMillis))).intValue();
    }

    public static int getCurrentYear() {
        return Integer.valueOf(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date())).intValue();
    }

    public static String formatDateTime(long timeInMillis) {
        if (isToday(timeInMillis)) {
            return formatTime(timeInMillis);
        }
        return formatDate(timeInMillis);
    }

    public static String formatDate(long timeInMillis) {
        return new SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Long.valueOf(timeInMillis));
    }

    public static boolean isToday(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(Long.valueOf(timeInMillis)).equals(dateFormat.format(Long.valueOf(System.currentTimeMillis())));
    }

    public static boolean hasSameDate(long millisFirst, long millisSecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(Long.valueOf(millisFirst)).equals(dateFormat.format(Long.valueOf(millisSecond)));
    }

    public static long getNegativeTimeStamp() {
        return getTimeStamp() * -1;
    }

    public static long getTimeStamp() {
        return new Date().getTime();
    }

    public static String formatLongDateTime(long timeInMillis) {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("E dd-MM-yyyy 'at' hh:mm:ss a", Locale.getDefault());
        return ft.format(timeInMillis);
    }
}
