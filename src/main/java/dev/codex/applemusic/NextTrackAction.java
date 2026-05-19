package dev.codex.applemusic;

import com.intellij.openapi.project.Project;

public final class NextTrackAction extends BaseAppleMusicAction {
    @Override
    protected void run(Project project) {
        AppleMusicBridge.runAsync(
            project,
            "Apple Music",
            AppleMusicScripts.NEXT_TRACK,
            "Skipped to the next track."
        );
    }
}
