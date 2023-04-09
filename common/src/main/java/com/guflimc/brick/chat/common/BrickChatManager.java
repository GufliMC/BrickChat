package com.guflimc.brick.chat.common;

import com.guflimc.brick.chat.api.ChatManager;
import com.guflimc.brick.chat.api.channel.ChatChannel;
import com.guflimc.brick.chat.api.event.PlayerChannelChatEvent;
import net.kyori.adventure.text.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Pattern;

public abstract class BrickChatManager<T, U extends PlayerChannelChatEvent<T>> implements ChatManager<T> {

    private final Set<ChatChannel<T>> chatChannels = new CopyOnWriteArraySet<>();

    @Override
    public void registerChatChannel(ChatChannel<T> channel) {
        if (channelByName(channel.name()).isPresent()) {
            throw new IllegalArgumentException("A channel with that name already exists.");
        }
        chatChannels.add(channel);
    }

    @Override
    public void unregisterChatChannel(ChatChannel<T> channel) {
        chatChannels.remove(channel);
    }

    @Override
    public Optional<ChatChannel<T>> channelByName(String name) {
        return chatChannels.stream().filter(c -> c.name().equals(name))
                .findFirst();
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
        return format(format, player, Component.text(message));
    }

    protected Component format(Component format, T player, Component message) {
        return format
                .replaceText(builder -> {
                    builder.match(Pattern.quote("%chatmessage%"))
                            .replacement(message);
                });
    }

    protected U dispatch(T entity, String message) {
        message = message.trim();

        List<ChatChannel<T>> channels = channels().stream()
                .filter(c -> c.canTalk(entity))
                .sorted(Comparator.comparingInt(ch -> ch.activator() == null ? 0 : -ch.activator().length()))
                .toList();

        // initialize default channel
        ChatChannel<T> channel = defaultChannel(entity).orElse(null);

        // unset default chat channel by just typing the prefix
        if (channel != null && channel.activator() != null && channel.activator().equals(message)) {
            unsetDefaultChannel(entity);
            return null;
        }

        // set default chat channel by just typing the prefix
        for (ChatChannel<T> ch : channels) {
            if (ch.activator() != null && !ch.activator().equals("") && ch.activator().equals(message)) {
                setDefaultChannel(entity, ch);
                return null;
            }
        }

        return dispatch(entity, message, channel);
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
