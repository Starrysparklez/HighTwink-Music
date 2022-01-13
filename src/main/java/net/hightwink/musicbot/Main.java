package net.hightwink.musicbot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
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
import net.hightwink.musicbot.commands.Pause;
import net.hightwink.musicbot.commands.Restart;
import net.hightwink.musicbot.exceptions.NonImportantException;
import net.hightwink.musicbot.classes.SlashCommandExecutor;
import net.hightwink.musicbot.commands.Play;
import net.hightwink.musicbot.commands.Stop;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

public class Main extends ListenerAdapter {
    SlashCommandExecutor[] executors;
    HashMap<String, Object> config = new HashMap<>();

    public static void main(String[] args) {
        try {
            new Main().run();
        } catch(IOException err) {
            err.printStackTrace();
        }
    }

    public void run() throws IOException {
        executors = new SlashCommandExecutor[]{
                new Play(), new Stop(), new Pause(), new Restart()
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

        for (SlashCommandExecutor cmd : executors)
            e.getJDA().getGuilds().forEach(g -> {
                e.getJDA().upsertCommand(cmd.commandData).queue();
                g.upsertCommand(cmd.commandData).queue();
            });
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
