package net.hightwink.musicbot.classes;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

import javax.annotation.Nonnull;
import java.util.*;

public class Context {
    private final Map<String, Object> config;
    private final InteractionHook hook;
    private final SlashCommandEvent event;

    public Context(SlashCommandEvent event, Map<String, Object> config, InteractionHook hook) {
        this.config = config;
        this.event = event;
        this.hook = hook;
    }
    @Nonnull
    public Map<String, Object> getConfig() {
        return this.config;
    }
    @Nonnull
    public Member getAuthor() {
        assert this.event.getMember() != null;
        return this.event.getMember();
    }
    @Nonnull
    public Guild getGuild() {
        assert this.event.getGuild() != null;
        return this.event.getGuild();
    }
    @Nonnull
    public WebhookMessageAction<Message> replyText(String content) {
        return this.hook.sendMessage(content
                .replace("@here", "@\u200bhere")
                .replace("@everyone", "@\u200beveryone"));
    }
    @Nonnull
    public WebhookMessageAction<Message> replyEmbeds(MessageEmbed... embeds) {
        return this.hook.sendMessageEmbeds(Arrays.asList(embeds));
    }
    @Nonnull
    public SlashCommandEvent getEvent() {
        return this.event;
    }
}
