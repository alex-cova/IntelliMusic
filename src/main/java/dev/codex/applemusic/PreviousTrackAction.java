package dev.codex.applemusic;

import com.intellij.openapi.project.Project;

public final class PreviousTrackAction extends BaseAppleMusicAction {
    @Override
    protected void run(Project project) {
        AppleMusicBridge.runAsync(
            project,
            "Apple Music",
            AppleMusicScripts.PREVIOUS_TRACK,
            "Returned to the previous track."
        );
    }
}
