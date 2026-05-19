package dev.codex.applemusic;

import com.intellij.openapi.project.Project;

public final class CurrentTrackAction extends BaseAppleMusicAction {
    @Override
    protected void run(Project project) {
        AppleMusicBridge.runAsync(
            project,
            "Current Track",
            AppleMusicScripts.CURRENT_TRACK,
            "No current track."
        );
    }
}
