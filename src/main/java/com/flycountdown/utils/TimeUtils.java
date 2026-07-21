/*
 * Decompiled with CFR 0.152.
 */
package com.flycountdown.utils;

public class TimeUtils {
    public static long parseTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return -1L;
        }
        timeString = timeString.toLowerCase().trim();
        StringBuilder numberStr = new StringBuilder();
        int unit = 115;
        for (int n : timeString.toCharArray()) {
            if (!Character.isDigit((char)n)) {
                if (n == 115 || n == 109 || n == 104 || n == 100) {
                    unit = n;
                    break;
                }
                return -1L;
            }
            numberStr.append((char)n);
        }
        if (numberStr.length() == 0) {
            return -1L;
        }
        try {
            long number = Long.parseLong(numberStr.toString());
            switch (unit) {
                case 115: {
                    return number;
                }
                case 109: {
                    return number * 60L;
                }
                case 104: {
                    return number * 3600L;
                }
                case 100: {
                    return number * 86400L;
                }
            }
            return -1L;
        }
        catch (NumberFormatException e) {
            return -1L;
        }
    }

    public static String formatTime(long seconds) {
        if (seconds < 0L) {
            return "0s";
        }
        if (seconds == 0L) {
            return "0s";
        }
        long days = seconds / 86400L;
        long hours = seconds % 86400L / 3600L;
        long minutes = seconds % 3600L / 60L;
        long secs = seconds % 60L;
        StringBuilder result = new StringBuilder();
        if (days > 0L) {
            result.append(days).append("d");
            if (hours > 0L || minutes > 0L || secs > 0L) {
                result.append(" ");
            }
        }
        if (hours > 0L) {
            result.append(hours).append("h");
            if (minutes > 0L || secs > 0L) {
                result.append(" ");
            }
        }
        if (minutes > 0L) {
            result.append(minutes).append("m");
            if (secs > 0L) {
                result.append(" ");
            }
        }
        if (secs > 0L || result.length() == 0) {
            result.append(secs).append("s");
        }
        return result.toString().trim();
    }

    public static String formatDigital(long seconds) {
        if (seconds < 0L) {
            seconds = 0L;
        }
        long hours = seconds / 3600L;
        long minutes = seconds % 3600L / 60L;
        long secs = seconds % 60L;
        if (hours > 0L) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        }
        return String.format("%02d:%02d", minutes, secs);
    }
}

