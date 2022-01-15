package net.hightwink.musicbot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.hightwink.musicbot.classes.Context;
import net.hightwink.musicbot.classes.SlashCommandExecutor;
import net.hightwink.musicbot.classes.audio.AudioManager;
import net.hightwink.musicbot.classes.audio.MusicPlayerManager;
import net.hightwink.musicbot.classes.utils.VoteManager;

public class Shuffle extends SlashCommandExecutor {
    public Shuffle() {
        this.commandName = "shuffle";
        this.description = "Перемешать треки в очереди.";
        this.commandData = new CommandData(this.commandName, this.description);
    }

    @Override
    public void execute(Context ctx) {
        if (ctx.getAuthor().getPermissions().contains(Permission.ADMINISTRATOR)) {
            shufflePlayback(ctx);
            return;
        }
        VoteManager voteManager = new VoteManager(ctx);
        voteManager.call("Голосование за перемешивание очереди", c -> shufflePlayback(ctx),
                c -> ctx.replyText("Запрос на перемешивание очереди отменен."));
    }

    private void shufflePlayback(Context ctx) {
        MusicPlayerManager manager = AudioManager.get(ctx.getGuild());
        manager.getScheduler().shuffleQueue();
        ctx.replyText(":twisted_rightwards_arrows: :ok_hand:");
    }
}
