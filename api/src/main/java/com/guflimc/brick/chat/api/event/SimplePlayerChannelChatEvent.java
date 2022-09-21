package com.guflimc.brick.chat.api.event;

import com.guflimc.brick.chat.api.channel.ChatChannel;
import net.kyori.adventure.text.Component;

import java.util.Set;

public class SimplePlayerChannelChatEvent<T>  implements PlayerChannelChatEvent<T> {

    private final T player;

    private final ChatChannel<T> chatChannel;
    private final String message;
    private final Set<T> recipients;

    private Component format;

    private boolean cancelled = false;

    public SimplePlayerChannelChatEvent(T player, ChatChannel<T> chatChannel, String message, Set<T> recipients, Component format) {
        this.player = player;
        this.chatChannel = chatChannel;
        this.message = message;
        this.recipients = recipients;
        this.format = format;
    }

    public ChatChannel<T> chatChannel() {
        return chatChannel;
    }

    public String message() {
        return message;
    }

    public Component format() {
        return format;
    }

    public Set<T> recipients() {
        return recipients;
    }

    public void setFormat(Component format) {
        this.format = format;
    }

    public T player() {
        return player;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
