package net.hightwink.musicbot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.hightwink.musicbot.classes.Context;
import net.hightwink.musicbot.classes.SlashCommandExecutor;

public class Exit extends SlashCommandExecutor {
    public Exit() {
        this.commandName = "exit";
        this.description = "Завершить работу данного инстанса бота.";
        this.commandData = new CommandData(this.commandName, this.description);
        this.applyToStuffServer = true;
    }

    @Override
    public void execute(Context ctx) {
        if (ctx.getAuthor().getPermissions().contains(Permission.ADMINISTRATOR)) {
            ctx.replyText("Выключаемся...");
            System.exit(0);
        }
    }
}
