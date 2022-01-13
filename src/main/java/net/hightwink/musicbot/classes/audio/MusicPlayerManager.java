package net.hightwink.musicbot.classes.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;

public class MusicPlayerManager {
    private final AudioPlayer player;
    private final TrackScheduler scheduler;

    public MusicPlayerManager(AudioPlayerManager audioPlayerManager, Guild guild) {
        player = audioPlayerManager.createPlayer();
        scheduler = new TrackScheduler(player, guild);
        player.addListener(scheduler);
    }

    /**
     * Get the player object for the current server.
     * @return AudioPlayer object.
     */
    public AudioPlayer getPlayer() {
        return player;
    }

    /**
     * Get the TaskScheduler for the player on this server.
     * @return TaskScheduler object.
     */
    public TrackScheduler getScheduler() {
        return scheduler;
    }
}
