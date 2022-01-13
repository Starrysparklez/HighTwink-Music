package net.hightwink.musicbot.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.hightwink.musicbot.classes.Context;
import net.hightwink.musicbot.classes.SlashCommandExecutor;

public class Restart extends SlashCommandExecutor {
    public Restart() {
        this.commandName = "restart";
        this.description = "Завершить работу данного инстанса бота и запустить заново, "
                + "используя файл, указанный в команде.";
        this.commandData = new CommandData(this.commandName, this.description)
                .addOption(OptionType.STRING, "jarfile",
                        "Jar-файл, который необходимо запустить по"
                                + "завершению работы данного инстанса бота.", true);
    }

    @Override
    public void execute(Context ctx) {
        ctx.replyText("Не готово.").queue();
    }
}
