package com.guflimc.brick.chat.minestom.api.channel;

import com.guflimc.brick.chat.api.channel.AbstractChatChannel;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

public class MinestomChatChannel extends AbstractChatChannel<Player> {

    public MinestomChatChannel(String name, String activator, Component format, boolean restricted) {
        super(name, activator, format, restricted);
    }

    @Override
    public boolean canRead(Player player) {
        if ( restricted ) {
            return player.hasPermission("brickchat.channel." + name + ".read")
                    || canTalk(player);
        }
        return canTalk(player);
    }

    @Override
    public boolean canTalk(Player player) {
        if ( restricted ) {
            return player.hasPermission("brickchat.channel." + name + ".talk")
                    || player.getPermissionLevel() == 4;
        }
        return true;
    }

}
