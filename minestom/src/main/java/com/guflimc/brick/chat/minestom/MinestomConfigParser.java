package com.guflimc.brick.chat.minestom;

import com.guflimc.brick.chat.api.ChatManager;
import com.guflimc.brick.chat.api.channel.AbstractRestrictedChatChannel;
import com.guflimc.brick.chat.common.ConfigParser;
import com.guflimc.brick.chat.minestom.api.channel.MinestomChatChannel;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

public class MinestomConfigParser extends ConfigParser<Player> {

    public MinestomConfigParser(ChatManager<Player> chatManager) {
        super(chatManager);
    }

    @Override
    protected AbstractRestrictedChatChannel<Player> createChannel(String name, String activator, Component format) {
        return new MinestomChatChannel(name, activator, format);
    }

}
