package com.guflimc.brick.chat.spigot.api.channel;

import com.guflimc.brick.chat.api.channel.AbstractRestrictedChatChannel;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class SpigotChatChannel extends AbstractRestrictedChatChannel<Player> {

    public SpigotChatChannel(String name, String activator, Component format) {
        super(name, activator, format);
    }

    @Override
    public boolean canRead(Player player) {
        if ( action == RestrictedAction.READ_AND_TALK ) {
            return player.hasPermission("brickchat.channel." + name) || player.isOp();
        }
        return true;
    }

    @Override
    public boolean canTalk(Player player) {
        if ( action == RestrictedAction.TALK ) {
            return player.hasPermission("brickchat.channel." + name) || player.isOp();
        }
        return canRead(player);
    }

}
