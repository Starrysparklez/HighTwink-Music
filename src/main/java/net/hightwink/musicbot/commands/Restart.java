package net.hightwink.musicbot.commands;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.hightwink.musicbot.classes.Context;
import net.hightwink.musicbot.classes.SlashCommandExecutor;

public class Restart extends SlashCommandExecutor {
    public Restart() {
        this.commandName = "exit";
        this.description = "Завершить работу данного инстанса бота.";
        this.commandData = new CommandData(this.commandName, this.description);
        this.applyToStuffServer = true;
    }

    @Override
    public void execute(Context ctx) {
        ctx.replyText("Выключаемся...").queue();
        System.exit(0);
    }
}
