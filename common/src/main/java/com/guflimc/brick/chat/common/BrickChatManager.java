package com.guflimc.brick.chat.common;

import com.guflimc.brick.chat.api.ChatManager;
import com.guflimc.brick.chat.api.channel.ChatChannel;
import com.guflimc.brick.chat.api.event.PlayerChannelChatEvent;
import net.kyori.adventure.text.Component;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Pattern;

public abstract class BrickChatManager<T, U extends PlayerChannelChatEvent<T>> implements ChatManager<T> {

    private final Set<ChatChannel<T>> chatChannels = new CopyOnWriteArraySet<>();

    @Override
    public void registerChatChannel(ChatChannel<T> channel) {
        if (channelByName(channel.name()) != null) {
            throw new IllegalArgumentException("A channel with that name already exists.");
        }
        chatChannels.add(channel);
    }

    @Override
    public void unregisterChatChannel(ChatChannel<T> channel) {
        chatChannels.remove(channel);
    }

    @Override
    public ChatChannel<T> channelByName(String name) {
        return chatChannels.stream().filter(c -> c.name().equals(name))
                .findFirst().orElse(null);
    }

    @Override
    public Collection<ChatChannel<T>> channels() {
        return Collections.unmodifiableCollection(chatChannels);
    }
    @Override
    public void execute(T entity, String message) {
        U event = dispatch(entity, message);
        if (event == null) return;
        send(event.recipients(), event.player(), event.format(), event.message());
    }

    @Override
    public abstract void send(ChatChannel<T> channel, T player, String message);

    @Override
    public abstract void send(ChatChannel<T> channel, Component text);

    protected abstract void send(Collection<T> recipients, T player, Component format, String message);

    protected Component format(Component format, T player, String message) {
        return format
                .replaceText(builder -> {
                    builder.match(Pattern.quote("{chatmessage}"))
                            .replacement(Component.text(message));
                });
    }

    protected U dispatch(T entity, String message) {
        return dispatch(entity, message, null);
    }

    protected U dispatch(T entity, String message, ChatChannel<T> defaultChannel) {
        message = message.trim();

        // initialize default channel
        ChatChannel<T> channel = defaultChannel;

        // get available channels for entity
        List<ChatChannel<T>> channels = chatChannels.stream()
                .filter(c -> c.canTalk(entity))
                .sorted(Comparator.comparingInt(ch -> ch.activator() == null ? 0 : -ch.activator().length()))
                .toList();

        // find channel for typed prefix
        for (ChatChannel<T> ch : channels) {
            if (ch == channel) {
                continue;
            }

            if (ch.activator() != null && !message.startsWith(ch.activator())) {
                continue;
            }

            // If the player is using a default channel, use that prefix instead to talk in the channel without prefix
            if (channel != null && channel.activator() != null && ch.activator() != null && ch.activator().equals("")) {
                if (message.startsWith(channel.activator())) {
                    message = message.replaceFirst(Pattern.quote(channel.activator()), "");
                    channel = ch;
                    break;
                }
                continue;
            }

            if (ch.activator() != null) {
                message = message.substring(ch.activator().length());
            }

            channel = ch;
            break;
        }

        if (channel == null) {
            return null;
        }

        U channelChatEvent = dispatch(channel, entity, message);
        if (channelChatEvent.isCancelled() || channelChatEvent.recipients().isEmpty()) {
            return null;
        }

        return channelChatEvent;
    }

    protected abstract U dispatch(ChatChannel<T> channel, T entity, String message);
}
