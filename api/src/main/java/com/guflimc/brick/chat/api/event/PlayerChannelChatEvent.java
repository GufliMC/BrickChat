package com.guflimc.brick.chat.api.event;

import com.guflimc.brick.chat.api.channel.ChatChannel;
import net.kyori.adventure.text.Component;

import java.util.Set;

public interface PlayerChannelChatEvent<T> {

    ChatChannel<T> chatChannel();

    String message();

    Component format();

    Set<T> recipients();

    void setFormat(Component format);

    T player();

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
