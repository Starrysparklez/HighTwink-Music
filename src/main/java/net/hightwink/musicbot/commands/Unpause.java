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

public class Unpause extends SlashCommandExecutor {
    public Unpause() {
        this.commandName = "unpause";
        this.description = "Снять воспроизведение с паузы.";
        this.commandData = new CommandData(this.commandName, this.description);
    }

    @Override
    public void execute(Context ctx) {
        if (ctx.getAuthor().getPermissions().contains(Permission.ADMINISTRATOR)) {
            unpausePlayback(ctx);
            return;
        }
        VoteManager voteManager = new VoteManager(ctx);
        voteManager.call("Голосование за возобновление проигрывания", c -> unpausePlayback(ctx),
                c -> ctx.replyText("Запрос на возобновление проигрывания."));
    }

    public void unpausePlayback(Context ctx) {
        MusicPlayerManager manager = AudioManager.get(ctx.getGuild());
        AudioPlayer player = manager.getPlayer();
        if (!player.isPaused()) {
            ctx.replyText("Воспроизведение и так идет... Что ты хочешь возобновить? :thinking:");
            return;
        }
        player.setPaused(false);
        ctx.replyText("Воспроизведение возобновлено!");
    }
}
