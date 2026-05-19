package dev.codex.applemusic;

import java.util.Locale;

record AppleMusicStatus(
    String state,
    String track,
    String artist,
    String album,
    double positionSeconds,
    double durationSeconds,
    String playlist
) {
    static AppleMusicStatus empty(String state) {
        return new AppleMusicStatus(state, "", "", "", 0, 0, "");
    }

    static AppleMusicStatus parse(String output) {
        String[] parts = output.split("\\t", -1);
        if (parts.length < 7) {
            return empty("unknown");
        }

        return new AppleMusicStatus(
            parts[0],
            parts[1],
            parts[2],
            parts[3],
            parseDouble(parts[4]),
            parseDouble(parts[5]),
            parts[6]
        );
    }

    boolean hasTrack() {
        return !track.isBlank();
    }

    String stateLabel() {
        if (state == null || state.isBlank()) {
            return "Unknown";
        }

        return state.substring(0, 1).toUpperCase(Locale.ROOT) + state.substring(1).replace('_', ' ');
    }

    String timeLabel() {
        return formatTime(positionSeconds) + " / " + formatTime(durationSeconds);
    }

    int progressPercent() {
        if (durationSeconds <= 0) {
            return 0;
        }

        return Math.max(0, Math.min(100, (int) Math.round((positionSeconds / durationSeconds) * 100)));
    }

    private static double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private static String formatTime(double totalSeconds) {
        int seconds = Math.max(0, (int) Math.round(totalSeconds));
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format(Locale.ROOT, "%d:%02d", minutes, remainingSeconds);
    }
}
