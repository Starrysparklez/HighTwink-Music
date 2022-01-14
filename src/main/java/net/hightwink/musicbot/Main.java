package net.hightwink.musicbot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.hightwink.musicbot.classes.Context;
import net.hightwink.musicbot.commands.*;
import net.hightwink.musicbot.exceptions.NonImportantException;
import net.hightwink.musicbot.classes.SlashCommandExecutor;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main extends ListenerAdapter {
    SlashCommandExecutor[] executors;
    HashMap<String, Object> config = new HashMap<>();
    static List<String> commandLineArgs;

    public static void main(String[] args) {
        try {
            commandLineArgs = Arrays.asList(args);
            new Main().run();
        } catch(IOException err) {
            err.printStackTrace();
        }
    }

    public void run() throws IOException {
        executors = new SlashCommandExecutor[]{
                new Play(), new Stop(), new Pause(), new Restart(), new Shuffle()
        };

        Yaml configParser = new Yaml();
        try {
            this.config = configParser.load(new InputStreamReader(new FileInputStream("./config.yml")));
            System.out.println("Конфигурация загружена.");
        } catch (FileNotFoundException error) {
            Files.copy(
                    Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.yml")),
                    Paths.get(new File("").getAbsolutePath() + "/config.yml"));
            System.out.println("Не удалось загрузить конфигурацию: файл отсутствует. Создаем новый...");
            System.exit(0);
        }


        try {
            JDABuilder.create((String) config.get("bot.token"),
                            GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                    .setDisabledIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS)
                    .setMemberCachePolicy(MemberCachePolicy.VOICE)
                    .addEventListeners(this)
                    .setCompression(Compression.ZLIB)
                    .setChunkingFilter(ChunkingFilter.NONE)
                    .setActivity(Activity.listening((String) config.get("bot.activity")))
                    .setStatus(OnlineStatus.ONLINE)
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent e) {
        System.out.println("Бот " + e.getJDA().getSelfUser().getName() + " готов к работе.");

        if (commandLineArgs.size() > 0 && commandLineArgs.get(0).equals("--unregister")) {
            Guild stuffGuild = e.getJDA().getGuildById((String) config.get("bot.stuffServerId"));
            assert stuffGuild != null;

            stuffGuild.retrieveCommands().complete().forEach(cmd -> {
                cmd.delete().queue();
                System.out.println("Unregistering stuff command '" + cmd.getName() + "'...");
            });
            e.getJDA().retrieveCommands().complete().forEach(cmd -> {
                cmd.delete().queue();
                System.out.println("Unregistering global command '" + cmd.getName() + "'...");
            });
        }
        if (commandLineArgs.size() > 0 && commandLineArgs.get(0).equals("--register")) {
            Guild stuffGuild = e.getJDA().getGuildById((String) config.get("bot.stuffServerId"));
            assert stuffGuild != null;

            // регистрация команд
            for (SlashCommandExecutor cmd : executors) {
                if (cmd.applyToStuffServer) {
                    // создать команду только на stuff сервере
                    stuffGuild.upsertCommand(cmd.commandData).queue();
                    System.out.println("Registering stuff command '" + cmd.commandName + "'...");
                } else {
                    // зарегать команду везде
                    e.getJDA().upsertCommand(cmd.commandData).queue();
                    System.out.println("Registering global command '" + cmd.commandName + "'...");
                }
            }
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        InteractionHook hook = event.deferReply(false).complete();
        Context ctx = new Context(event, config, hook);

        for (SlashCommandExecutor cmd : executors) {
            if (cmd.commandName.equals(event.getName())) {
                try {
                    cmd.execute(ctx);
                } catch(NonImportantException error) {
                    error.printStackTrace();
                }
            }
        }
    }
}
