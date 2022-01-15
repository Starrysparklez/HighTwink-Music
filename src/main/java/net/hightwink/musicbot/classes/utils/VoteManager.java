package net.hightwink.musicbot.classes.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.hightwink.musicbot.classes.Context;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.type.NullType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class VoteManager {
    Context ctx;

    public VoteManager(Context ctx) {
        this.ctx = ctx;
    }

    public void call(String voteFor, Consumer<NullType> callbackAccept, Consumer<NullType> callbackDecline) {
        GuildVoiceState voiceState = ctx.getGuild().getSelfMember().getVoiceState();
        if (voiceState == null || voiceState.getChannel() == null) {
            ctx.replyText("Ну, в войсе никого нет, так что вот так вот ...").queue();
            return;
        }
        GuildVoiceState userVoiceState = ctx.getAuthor().getVoiceState();
        if (userVoiceState == null || userVoiceState.getChannel() == null) {
            // || voiceState.getChannel().getMembers().contains(ctx.getAuthor())
            ctx.replyText("Вы не находитесь в общем со мной голосовом канале.").queue();
            return;
        }

        String yesButtonId = "yesButton" + UUID.randomUUID();
        String noButtonId = "yesButton" + UUID.randomUUID();

        List<Member> votedYes = new ArrayList<>();
        List<Member> votedNo  = new ArrayList<>();
        List<Member> voiceMembers = ctx.getGuild().getSelfMember().getVoiceState().getChannel().getMembers()
                .stream()
                .filter(member -> member.getIdLong() != ctx.getEvent().getJDA().getSelfUser().getIdLong())
                .collect(Collectors.toList());

        InteractionHook hook = ctx.getEvent().getHook();
        AtomicBoolean requestSatisfied = new AtomicBoolean(false);

        hook.sendMessageEmbeds(new EmbedBuilder()
                .setTitle(voteFor)
                .setColor(ctx.getAuthor().getColorRaw())
                .setDescription("Ну в общем типа нажмите зеленую кнопку если вы ЗА и красную если ПРОТИВ...да...")
                .build()
        ).addActionRows(ActionRow.of(
                Button.success(yesButtonId, String.format("ЗА [0/%d]", voiceMembers.size())),
                Button.danger(noButtonId, String.format("ПРОТИВ [0/%d]", voiceMembers.size()))
        )).queue(interaction -> {
            ListenerAdapter eventListener = new ListenerAdapter() {
                @Override
                public void onButtonClick(@NotNull ButtonClickEvent event) {
                    String customId = Objects.requireNonNull(event.getButton()).getId();
                    assert customId != null;

                    Member member = event.getMember();

                    if (customId.equals(yesButtonId)) {
                        votedYes.add(member);
                        votedNo.remove(member);
                    } else {
                        votedNo.add(member);
                        votedYes.remove(member);
                    }

                    if (votedNo.size() >= voiceMembers.size()) {
                        callbackDecline.accept(null);
                        requestSatisfied.set(true);
                    } else if (votedYes.size() >= voiceMembers.size()){
                        callbackAccept.accept(null);
                        requestSatisfied.set(true);
                    }

                    event.editComponents(ActionRow.of(
                            Button.success(yesButtonId, String.format("ЗА [%d/%d]",
                                    votedYes.size(), voiceMembers.size())),
                            Button.danger(noButtonId, String.format("ПРОТИВ [%d/%d]",
                                    votedNo.size(), voiceMembers.size())))
                    ).queue();
                }
            };

            new Thread(() -> {
                JDA jda = ctx.getEvent().getJDA();
                if (voiceMembers.size() > 1) {
                    jda.addEventListener(eventListener);
                    try { Thread.sleep(10 * 1000); } catch (InterruptedException e) { e.printStackTrace(); }
                    jda.removeEventListener(eventListener);
                } else {
                    callbackAccept.accept(null);
                    return;
                }
                if (!requestSatisfied.get()) callbackDecline.accept(null);
            }).start();
        });
    }
}
