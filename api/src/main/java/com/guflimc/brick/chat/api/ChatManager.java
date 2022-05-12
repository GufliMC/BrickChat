package com.guflimc.brick.chat.api;

import com.guflimc.brick.chat.api.channel.ChatChannel;
import net.kyori.adventure.text.Component;

import java.util.Collection;

public interface ChatManager<T> {

    void registerChatChannel(ChatChannel<T> channel);

    void unregisterChatChannel(ChatChannel<T> channel);

    ChatChannel<T> channelByName(String name);

    Collection<ChatChannel<T>> channels();

    void execute(T entity, String message);

    void send(ChatChannel<T> channel, Component text);

    void send(ChatChannel<T> channel, T entity, String message);

}
