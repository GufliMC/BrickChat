package com.guflimc.brick.chat.spigot;

import com.guflimc.brick.chat.api.ChatManager;
import com.guflimc.brick.chat.api.channel.AbstractRestrictedChatChannel;
import com.guflimc.brick.chat.common.ConfigParser;
import com.guflimc.brick.chat.spigot.api.channel.SpigotChatChannel;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class SpigotConfigParser extends ConfigParser<Player> {

    public SpigotConfigParser(ChatManager<Player> chatManager) {
        super(chatManager);
    }

    @Override
    protected AbstractRestrictedChatChannel<Player> createChannel(String name, String activator, Component format) {
        return new SpigotChatChannel(name, activator, format);
    }

}
