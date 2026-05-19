package dev.codex.applemusic;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

final class AppleMusicBridge {
    private static final String NOTIFICATION_GROUP_ID = "Apple Music Controls";
    private static final int TIMEOUT_SECONDS = 5;

    private AppleMusicBridge() {
    }

    static void runAsync(Project project, String title, String script, String successMessage) {
        runAsync(
            script,
            output -> {
                String content = output.isBlank() ? successMessage : output;
                notify(project, title, content, NotificationType.INFORMATION);
            },
            message -> notify(project, title, message, NotificationType.ERROR)
        );
    }

    static void runAsync(String script, Consumer<String> onSuccess, Consumer<String> onError) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                String output = runAppleScript(script);
                onSuccess.accept(output);
            } catch (AppleMusicException exception) {
                onError.accept(exception.getMessage());
            }
        });
    }

    private static String runAppleScript(String script) throws AppleMusicException {
        ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/osascript", "-e", script);

        try {
            Process process = processBuilder.start();
            boolean completed = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!completed) {
                process.destroyForcibly();
                throw new AppleMusicException("Apple Music did not respond in time.");
            }

            String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8).trim();

            if (process.exitValue() != 0) {
                throw new AppleMusicException(stderr.isBlank() ? "AppleScript command failed." : stderr);
            }

            return stdout;
        } catch (IOException exception) {
            throw new AppleMusicException("Could not run /usr/bin/osascript: " + exception.getMessage(), exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AppleMusicException("Apple Music command was interrupted.", exception);
        }
    }

    private static void notify(Project project, String title, String content, NotificationType type) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification(title, StringUtil.escapeXmlEntities(content), type)
            .notify(project);
    }

    private static final class AppleMusicException extends Exception {
        private AppleMusicException(String message) {
            super(message);
        }

        private AppleMusicException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
