package net.hightwink.musicbot.classes;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public abstract class SlashCommandExecutor {
    public String commandName;
    public String description;
    public CommandData commandData;
    public void execute(Context ctx) throws NonImportantException {};
}
