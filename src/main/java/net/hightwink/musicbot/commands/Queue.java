package net.hightwink.musicbot.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.hightwink.musicbot.classes.Context;
import net.hightwink.musicbot.classes.SlashCommandExecutor;
import net.hightwink.musicbot.classes.audio.AudioManager;
import net.hightwink.musicbot.classes.audio.MusicPlayerManager;

import java.util.ArrayList;

public class Queue extends SlashCommandExecutor {
    public Queue() {
        this.commandName = "queue";
        this.description = "Просмотр очереди воспроизведения и проигрываемого в данный момент трека.";
        this.commandData = new CommandData(this.commandName, this.description);
    }

    @Override
    public void execute(Context ctx) {
        MusicPlayerManager manager = AudioManager.get(ctx.getGuild());
        ArrayList<AudioTrack> queue = manager.getScheduler().getQueue();
        AudioPlayer player = manager.getPlayer();

        if (player.getPlayingTrack() == null) {
            ctx.replyText("Хм.. По-моему, музыка сейчас не проигрывается.\n"
                    + "Может быть, для начала начнете воспроизведение, используя команду `/play`?");
            return;
        }

        StringBuilder queueResponse = new StringBuilder();

        int index = 1;
        for (AudioTrack track : queue) {
            System.out.println(track.getInfo().title);
            queueResponse.append(String.format("%d. [%s](%s)", index,
                    track.getInfo().title, track.getInfo().uri)).append("\n");
            index++;
        }

        if (index == 1) queueResponse.append(
                "Очередь пуста... Добавьте больше треков, используя команду `/play`!");

        EmbedBuilder builder = new EmbedBuilder()
                .setDescription(String.format("Проигрывается:\n> **[%s](%s)**",
                        player.getPlayingTrack().getInfo().title, player.getPlayingTrack().getInfo().uri))
                .addField("Очередь:", queueResponse.toString(), false)
                .setColor(0xFFFFFF);

        ctx.replyEmbeds(builder.build());
    }
}
