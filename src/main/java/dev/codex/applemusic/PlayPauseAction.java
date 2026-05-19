package dev.codex.applemusic;

import com.intellij.openapi.project.Project;

public final class PlayPauseAction extends BaseAppleMusicAction {
    @Override
    protected void run(Project project) {
        AppleMusicBridge.runAsync(
            project,
            "Apple Music",
            AppleMusicScripts.PLAY_PAUSE,
            "Toggled playback."
        );
    }
}
