# Apple Music Controls for IntelliJ

Small IntelliJ Platform plugin for controlling Apple Music on macOS from the IDE.

## Features

- `Apple Music` tool window with playback controls, current track metadata, duration/progress, and current playlist view
- `Tools > Apple Music > Play/Pause`
- `Tools > Apple Music > Next Track`
- `Tools > Apple Music > Previous Track`
- `Tools > Apple Music > Show Current Track`

The plugin uses `/usr/bin/osascript` and AppleScript to talk to the macOS Music app.
AppleScript exposes the current playlist, but not Apple Music's full `Up Next` queue; the panel shows the current playlist starting at the current track.

## Requirements

- macOS
- IntelliJ IDEA 2026.1 or newer
- JDK 21 or newer. If JDK 21 is not installed, Gradle can auto-provision it through the configured Foojay toolchain resolver.
- Gradle 9 or newer

On first use, macOS may ask for Automation permission so IntelliJ can control Music. Allow it in `System Settings > Privacy & Security > Automation` if the prompt is missed.

## Build

```bash
gradle buildPlugin
```

The installable ZIP is written under:

```text
build/distributions/
```

## Run in a sandbox IDE

```bash
gradle runIde
```

## Install manually

In IntelliJ IDEA:

1. Open `Settings > Plugins`.
2. Open the gear menu.
3. Choose `Install Plugin from Disk...`.
4. Select the ZIP from `build/distributions/`.
