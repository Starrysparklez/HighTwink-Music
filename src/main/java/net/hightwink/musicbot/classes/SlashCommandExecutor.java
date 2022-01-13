package net.hightwink.musicbot.classes;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.hightwink.musicbot.exceptions.NonImportantException;

import java.util.List;

public abstract class SlashCommandExecutor {
    public String commandName;
    public String description;
    public CommandData commandData;
    public List<Permission> requiredPermissions;

    public void execute(Context ctx) throws NonImportantException {}
}
