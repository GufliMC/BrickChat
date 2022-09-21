package com.guflimc.brick.chat.api;

import com.guflimc.brick.chat.api.channel.ChatChannel;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.Optional;

public interface ChatManager<T> {

    void registerChatChannel(ChatChannel<T> channel);

    void unregisterChatChannel(ChatChannel<T> channel);

    Optional<ChatChannel<T>> channelByName(String name);

    Collection<ChatChannel<T>> channels();

    void execute(T entity, String message);

    void send(ChatChannel<T> channel, Component text);

    void send(ChatChannel<T> channel, T entity, String message);

    Optional<ChatChannel<T>> defaultChannel(T entity);

    void setDefaultChannel(T entity, ChatChannel<T> channel);

    void unsetDefaultChannel(T entity);

}
