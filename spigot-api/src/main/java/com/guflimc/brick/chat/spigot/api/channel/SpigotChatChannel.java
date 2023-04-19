package com.guflimc.brick.chat.spigot.api.channel;

import com.guflimc.brick.chat.api.channel.AbstractChatChannel;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class SpigotChatChannel extends AbstractChatChannel<Player> {

    public SpigotChatChannel(String name, Component format, String activator, boolean restricted) {
        super(name, format, activator, restricted);
    }

    @Override
    public boolean canRead(Player player) {
        if (restricted) {
            return player.hasPermission("brickchat.channel." + name + ".read") || canTalk(player);
        }
        return canTalk(player);
    }

    @Override
    public boolean canTalk(Player player) {
        if (restricted) {
            return player.hasPermission("brickchat.channel." + name + ".talk") || player.isOp();
        }
        return true;
    }

}
