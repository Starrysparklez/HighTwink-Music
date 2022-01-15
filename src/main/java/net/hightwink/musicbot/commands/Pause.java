package net.hightwink.musicbot.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.hightwink.musicbot.classes.Context;
import net.hightwink.musicbot.classes.SlashCommandExecutor;
import net.hightwink.musicbot.classes.audio.AudioManager;
import net.hightwink.musicbot.classes.audio.MusicPlayerManager;
import net.hightwink.musicbot.classes.utils.VoteManager;

public class Pause extends SlashCommandExecutor {
    public Pause() {
        this.commandName = "pause";
        this.description = "Поставить воспроизведение на паузу.";
        this.commandData = new CommandData(this.commandName, this.description);
    }

    @Override
    public void execute(Context ctx) {
        if (ctx.getAuthor().getPermissions().contains(Permission.ADMINISTRATOR)) {
            pausePlayback(ctx);
            return;
        }
        VoteManager voteManager = new VoteManager(ctx);
        voteManager.call("Голосование за паузу", c -> pausePlayback(ctx),
                c -> ctx.replyText("Запрос на паузу отменен."));
    }

    private void pausePlayback(Context ctx) {
        MusicPlayerManager manager = AudioManager.get(ctx.getGuild());
        AudioPlayer player = manager.getPlayer();
        if (player.isPaused()) {
            ctx.replyText("Воспроизведение и так на паузе.");
            return;
        }
        player.setPaused(true);
        ctx.replyText("Воспроизведение приостановлено!");
    }
}
