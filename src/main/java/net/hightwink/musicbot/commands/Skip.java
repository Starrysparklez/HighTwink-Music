package net.hightwink.musicbot.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.hightwink.musicbot.classes.Context;
import net.hightwink.musicbot.classes.SlashCommandExecutor;
import net.hightwink.musicbot.classes.audio.AudioManager;
import net.hightwink.musicbot.classes.audio.MusicPlayerManager;
import net.hightwink.musicbot.classes.audio.TrackScheduler;
import net.hightwink.musicbot.classes.utils.VoteManager;

public class Skip extends SlashCommandExecutor {
    public Skip() {
        this.commandName = "skip";
        this.description = "Пропустить текущий трек.";
        this.commandData = new CommandData(this.commandName, this.description);
    }

    @Override
    public void execute(Context ctx) {
        if (ctx.getAuthor().getPermissions().contains(Permission.ADMINISTRATOR)) {
            skipTrack(ctx);
            return;
        }
        VoteManager voteManager = new VoteManager(ctx);
        voteManager.call("Голосование за пропуск трека", c -> skipTrack(ctx),
                c -> ctx.replyText("Запрос на пропуск трека не выполнен."));
    }

    private void skipTrack(Context ctx) {
        MusicPlayerManager manager = AudioManager.get(ctx.getGuild());
        TrackScheduler scheduler = manager.getScheduler();

        AudioTrack track = scheduler.nextTrack();
        if (track == null) {
            manager.getPlayer().destroy();
            manager.getScheduler().clearQueue();
            GuildVoiceState vs = ctx.getGuild().getSelfMember().getVoiceState();

            if (vs != null && vs.getChannel() != null)
                ctx.getGuild().getAudioManager().closeAudioConnection();

            ctx.replyText("Воспроизведение окончено, т.к. достигнут конец очереди.");
            return;
        }
        ctx.replyText(String.format("Пропускаем... Следующий трек: **%s**", track.getInfo().title));
    }
}
