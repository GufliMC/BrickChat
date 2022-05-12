package com.guflimc.brick.chat.minestom.api.event;

import com.guflimc.brick.chat.api.channel.ChatChannel;
import com.guflimc.brick.chat.api.event.SimplePlayerChannelChatEvent;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.trait.CancellableEvent;

import java.util.Set;

public class MinestomPlayerChannelChatEvent extends SimplePlayerChannelChatEvent<Player> implements Event, CancellableEvent {

    public MinestomPlayerChannelChatEvent(Player entity, ChatChannel<Player> chatChannel, String message, Set<Player> recipients, Component format) {
        super(entity, chatChannel, message, recipients, format);
    }

}
