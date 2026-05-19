package dev.codex.applemusic;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;

abstract class BaseAppleMusicAction extends AnAction {
    @Override
    public final void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabled(SystemInfo.isMac);
    }

    @Override
    public final void actionPerformed(@NotNull AnActionEvent event) {
        if (!SystemInfo.isMac) {
            return;
        }

        run(event.getProject());
    }

    protected abstract void run(Project project);
}
