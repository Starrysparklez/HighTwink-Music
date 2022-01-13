package net.hightwink.musicbot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.hightwink.musicbot.classes.*;
import net.hightwink.musicbot.classes.audio.AudioManager;
import net.hightwink.musicbot.classes.audio.MusicPlayerManager;
import net.hightwink.musicbot.classes.utils.VoteManager;

public class Stop extends SlashCommandExecutor {
    public Stop() {
        this.commandName = "stop";
        this.description = "Остановить воспроизведение в голосовом канале.";
        this.commandData = new CommandData(commandName, description);
    }

    @Override
    public void execute(Context ctx) {
        if (ctx.getAuthor().getPermissions().contains(Permission.ADMINISTRATOR)) {
            stopPlayback(ctx);
            return;
        }
        VoteManager voteManager = new VoteManager(ctx);
        voteManager.call("Голосование за остановку воспроизведения", c -> stopPlayback(ctx),
                c -> ctx.replyText("Запрос на остановку воспроизведения не выполнен.").queue());
    }

    private void stopPlayback(Context ctx) {
        MusicPlayerManager manager = AudioManager.get(ctx.getGuild());
        manager.getScheduler().getQueue().clear();
        manager.getPlayer().destroy();
        ctx.getGuild().getAudioManager().closeAudioConnection();
    }
}
