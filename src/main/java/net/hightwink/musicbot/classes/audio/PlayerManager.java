package net.hightwink.musicbot.classes.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.*;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

public class PlayerManager {
    private static final AudioPlayerManager manager = new DefaultAudioPlayerManager();

    static {
        manager.registerSourceManager(new YoutubeAudioSourceManager(true));
        SoundCloudDataReader dataReader = new DefaultSoundCloudDataReader();
        SoundCloudHtmlDataLoader dataLoader = new DefaultSoundCloudHtmlDataLoader();
        SoundCloudFormatHandler formatHandler = new DefaultSoundCloudFormatHandler();
        manager.registerSourceManager(new SoundCloudAudioSourceManager(
                false,
                dataReader,
                dataLoader,
                formatHandler,
                new DefaultSoundCloudPlaylistLoader(dataLoader, dataReader, formatHandler)
        ));
    }
    public static AudioPlayerManager getManager() {
        return manager;
    }
}