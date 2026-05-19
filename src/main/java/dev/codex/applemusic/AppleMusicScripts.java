package dev.codex.applemusic;

final class AppleMusicScripts {
    static final String PLAY_PAUSE = """
        tell application "Music"
            playpause
        end tell
        """;

    static final String NEXT_TRACK = """
        tell application "Music"
            next track
        end tell
        """;

    static final String PREVIOUS_TRACK = """
        tell application "Music"
            previous track
        end tell
        """;

    static final String CURRENT_TRACK = """
        if application "Music" is running then
            tell application "Music"
                if player state is stopped then
                    return "Apple Music is stopped."
                end if

                set trackName to name of current track
                set artistName to artist of current track
                set albumName to album of current track

                if albumName is "" then
                    return trackName & " - " & artistName
                end if

                return trackName & " - " & artistName & " (" & albumName & ")"
            end tell
        end if

        return "Apple Music is not running."
        """;

    static final String STATUS = """
        set separator to ASCII character 9

        on safeText(valueText)
            try
                if valueText is missing value then return ""
                return valueText as text
            on error
                return ""
            end try
        end safeText

        if application "Music" is not running then
            return "not_running" & separator & "" & separator & "" & separator & "" & separator & "0" & separator & "0" & separator & ""
        end if

        tell application "Music"
            set stateText to player state as text

            if stateText is "stopped" then
                return stateText & separator & "" & separator & "" & separator & "" & separator & "0" & separator & "0" & separator & ""
            end if

            set trackName to my safeText(name of current track)
            set artistName to my safeText(artist of current track)
            set albumName to my safeText(album of current track)
            set playlistName to my safeText(name of current playlist)
            set positionSeconds to player position
            set durationSeconds to duration of current track

            return stateText & separator & trackName & separator & artistName & separator & albumName & separator & positionSeconds & separator & durationSeconds & separator & playlistName
        end tell
        """;

    static final String CURRENT_PLAYLIST_TRACKS = """
        set lineFeed to ASCII character 10

        on safeText(valueText)
            try
                if valueText is missing value then return ""
                return valueText as text
            on error
                return ""
            end try
        end safeText

        if application "Music" is not running then
            return ""
        end if

        tell application "Music"
            if player state is stopped then return ""

            set playlistTracks to tracks of current playlist
            set currentDatabaseId to database ID of current track
            set trackCount to count of playlistTracks
            set currentIndex to 1

            repeat with indexValue from 1 to trackCount
                if database ID of item indexValue of playlistTracks is currentDatabaseId then
                    set currentIndex to indexValue
                    exit repeat
                end if
            end repeat

            set startIndex to currentIndex
            set endIndex to currentIndex + 19
            if endIndex > trackCount then set endIndex to trackCount

            set outputText to ""
            repeat with indexValue from startIndex to endIndex
                set trackItem to item indexValue of playlistTracks
                set marker to "  "
                if indexValue is currentIndex then set marker to "> "

                set trackLine to marker & (my safeText(name of trackItem)) & " - " & (my safeText(artist of trackItem))
                if outputText is "" then
                    set outputText to trackLine
                else
                    set outputText to outputText & lineFeed & trackLine
                end if
            end repeat

            return outputText
        end tell
        """;

    private AppleMusicScripts() {
    }
}
