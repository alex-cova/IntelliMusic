package dev.codex.applemusic;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

final class AppleMusicToolWindowPanel extends JPanel {
    private final JLabel stateLabel = new JLabel("Not loaded");
    private final JLabel titleLabel = new JLabel("No track");
    private final JLabel artistLabel = new JLabel(" ");
    private final JLabel albumLabel = new JLabel(" ");
    private final JLabel playlistLabel = new JLabel(" ");
    private final JLabel timeLabel = new JLabel("0:00 / 0:00", SwingConstants.RIGHT);
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final DefaultListModel<String> queueModel = new DefaultListModel<>();
    private final Timer refreshTimer;

    AppleMusicToolWindowPanel(Project project) {
        super(new BorderLayout(12, 12));
        this.refreshTimer = new Timer(3_000, event -> refresh());

        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(createNowPlayingPanel(), BorderLayout.NORTH);
        add(createQueuePanel(), BorderLayout.CENTER);
        add(createControlsPanel(), BorderLayout.SOUTH);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        refresh();
        refreshTimer.start();
    }

    @Override
    public void removeNotify() {
        refreshTimer.stop();
        super.removeNotify();
    }

    private JPanel createNowPlayingPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        stateLabel.setFont(stateLabel.getFont().deriveFont(Font.BOLD));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));

        JPanel metadataPanel = new JPanel(new GridLayout(0, 1, 4, 4));
        metadataPanel.add(stateLabel);
        metadataPanel.add(titleLabel);
        metadataPanel.add(artistLabel);
        metadataPanel.add(albumLabel);
        metadataPanel.add(playlistLabel);

        JPanel progressPanel = new JPanel(new BorderLayout(8, 0));
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(timeLabel, BorderLayout.EAST);

        panel.add(metadataPanel, BorderLayout.CENTER);
        panel.add(progressPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createQueuePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JLabel queueLabel = new JLabel("Current playlist");
        queueLabel.setFont(queueLabel.getFont().deriveFont(Font.BOLD));

        JList<String> queueList = new JList<>(queueModel);
        queueList.setVisibleRowCount(12);

        panel.add(queueLabel, BorderLayout.NORTH);
        panel.add(new JBScrollPane(queueList), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(280, 220));
        return panel;
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 8, 0));

        JButton previousButton = new JButton("Previous");
        JButton playPauseButton = new JButton("Play/Pause");
        JButton nextButton = new JButton("Next");
        JButton refreshButton = new JButton("Refresh");

        previousButton.addActionListener(event -> runCommand(AppleMusicScripts.PREVIOUS_TRACK));
        playPauseButton.addActionListener(event -> runCommand(AppleMusicScripts.PLAY_PAUSE));
        nextButton.addActionListener(event -> runCommand(AppleMusicScripts.NEXT_TRACK));
        refreshButton.addActionListener(event -> refresh());

        panel.add(previousButton);
        panel.add(playPauseButton);
        panel.add(nextButton);
        panel.add(refreshButton);
        return panel;
    }

    private void runCommand(String script) {
        AppleMusicBridge.runAsync(
            script,
            output -> SwingUtilities.invokeLater(this::refresh),
            message -> SwingUtilities.invokeLater(() -> showError(message))
        );
    }

    private void refresh() {
        AppleMusicBridge.runAsync(
            AppleMusicScripts.STATUS,
            output -> {
                AppleMusicStatus status = AppleMusicStatus.parse(output);
                SwingUtilities.invokeLater(() -> updateStatus(status));
            },
            message -> SwingUtilities.invokeLater(() -> showError(message))
        );

        AppleMusicBridge.runAsync(
            AppleMusicScripts.CURRENT_PLAYLIST_TRACKS,
            output -> SwingUtilities.invokeLater(() -> updateQueue(output)),
            message -> SwingUtilities.invokeLater(() -> updateQueue(""))
        );
    }

    private void updateStatus(AppleMusicStatus status) {
        stateLabel.setText(status.stateLabel());
        titleLabel.setText(status.hasTrack() ? status.track() : "No track");
        artistLabel.setText(status.artist().isBlank() ? " " : status.artist());
        albumLabel.setText(status.album().isBlank() ? " " : status.album());
        playlistLabel.setText(status.playlist().isBlank() ? " " : "Playlist: " + status.playlist());
        timeLabel.setText(status.timeLabel());
        progressBar.setValue(status.progressPercent());
    }

    private void updateQueue(String output) {
        queueModel.clear();

        if (output == null || output.isBlank()) {
            queueModel.addElement("No current playlist available.");
            return;
        }

        for (String line : output.split("\\R")) {
            if (!line.isBlank()) {
                queueModel.addElement(line);
            }
        }
    }

    private void showError(String message) {
        stateLabel.setText("Error");
        titleLabel.setText(message);
        artistLabel.setText(" ");
        albumLabel.setText(" ");
        playlistLabel.setText(" ");
        timeLabel.setText("0:00 / 0:00");
        progressBar.setValue(0);
        updateQueue("");
    }
}
