package net.hightwink.musicbot.classes.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.Collections;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final ArrayList<AudioTrack> queue;
    private final Guild guild;

    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.guild = guild;
        this.queue = new ArrayList<>();
    }
    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.add(track);
        }
    }
    public void clearQueue() {
        queue.clear();
    }
    public AudioTrack nextTrack() {
        AudioTrack track = queue.size() > 0 ? queue.get(0) : null;
        if (track != null) {
            queue.remove(0);
            player.startTrack(track, false);
        }
        return track;
    }
    public ArrayList<AudioTrack> getQueue() {
        return queue;
    }
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason reason) {
        if (reason.mayStartNext) {
            if (nextTrack() == null) {
                player.destroy();
                clearQueue();
                if (guild.getAudioManager().getConnectionStatus() == ConnectionStatus.CONNECTED)
                    guild.getAudioManager().closeAudioConnection();
            }
        }
    }
    public void shuffleQueue() {
        Collections.shuffle(queue);
    }
}
