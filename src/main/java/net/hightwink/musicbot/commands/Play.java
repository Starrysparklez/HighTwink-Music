package net.hightwink.musicbot.commands;

import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.hightwink.musicbot.classes.*;
import net.hightwink.musicbot.classes.audio.AudioManager;
import net.hightwink.musicbot.classes.audio.AudioPlayerSendHandler;
import net.hightwink.musicbot.classes.audio.MusicPlayerManager;
import net.hightwink.musicbot.classes.audio.PlayerManager;

import java.util.List;
import java.util.Objects;

public class Play extends SlashCommandExecutor {
    public Play() {
        this.commandName = "play";
        this.description = "Начать проигрывание музыки в голосовом канале.";
        this.commandData = new CommandData(commandName, description)
                .addOption(OptionType.STRING, "query", "Ваш запрос", true);
    }

    @Override
    public void execute(Context ctx) {
        GuildVoiceState userState = ctx.getAuthor().getVoiceState();
        if (userState == null || userState.getChannel() == null) {
            ctx.replyText("Вы должны находиться в голосовом канале!").queue();
            return;
        }

        List<String> allowedVoiceChannelIds = (List<String>) ctx.getConfig().get("bot.allowedVoices");
        if (allowedVoiceChannelIds.size() > 0
                && !allowedVoiceChannelIds.contains(userState.getChannel().getId())) {
            ctx.replyText("Проигрывание музыки запрещено в этом канале.").queue();
            return;
        }

        Guild server = ctx.getGuild();
        String query = Objects.requireNonNull(ctx.getEvent().getOption("query")).getAsString();

        MusicPlayerManager manager = AudioManager.get(ctx.getGuild());
        ctx.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(manager.getPlayer()));

        server.getAudioManager().openAudioConnection(userState.getChannel());

        PlayerManager.getManager().loadItemOrdered(manager,
            query.startsWith("https://") ? query : "ytsearch: " + query,
            new FunctionalResultHandler(audioTrack -> {
                ctx.replyText("Трек **" + audioTrack.getInfo().title + "** добавлен в очередь.")
                        .queue();
                manager.getScheduler().queue(audioTrack);
            }, audioPlaylist -> {
                if (audioPlaylist.isSearchResult()) {
                    AudioTrack first = audioPlaylist.getTracks().get(0);
                    manager.getScheduler().queue(first);
                    ctx.replyText("Трек **" + first.getInfo().title + "** добавлен в очередь.")
                            .queue();
                } else {
                    audioPlaylist.getTracks().forEach(t -> {
                        if (manager.getScheduler().getQueue().size() + 1 > 200) {
                            ctx.replyText("Размер очереди превышен. Пожалуйста, не более 200 треков.")
                                    .queue();
                            return;
                        }
                        manager.getScheduler().queue(t);
                    });
                ctx.replyText("Плейлист **" + audioPlaylist.getName() + "** с "
                                + audioPlaylist.getTracks().size() + " треками добавлен в очередь.")
                        .queue();
                }
            }, () -> ctx.replyText("По запросу " + query + " ничего не найдено.").queue(),
            error -> {
                error.printStackTrace();
                ctx.replyText("Ошибка воспроизведения.").queue();
            })
        );
    }
}
