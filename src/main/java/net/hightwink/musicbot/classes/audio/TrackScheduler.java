package net.hightwink.musicbot.classes;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.Guild;

import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final LinkedBlockingQueue<AudioTrack> queue;
    private final Guild guild;
    public boolean repeatMode = false;

    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.guild = guild;
        this.queue = new LinkedBlockingQueue<>();
    }
    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }
    public void clearQueue() {
        queue.clear();
    }
    public AudioTrack nextTrack() {
        AudioTrack track = queue.poll();
        player.startTrack(track, false);
        return track;
    }
    public LinkedBlockingQueue<AudioTrack> getQueue() {
        return queue;
    }
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason) {
        if (repeatMode && reason == AudioTrackEndReason.FINISHED) {
            player.startTrack(track.makeClone(), false);
            return;
        }
        if (reason.mayStartNext) {
            if (nextTrack() == null) {
                player.destroy();
                clearQueue();
                if (guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTED)
                    guild.getAudioManager().closeAudioConnection();
            }
        }
    }
}
