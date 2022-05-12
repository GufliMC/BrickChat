package com.guflimc.brick.chat.minestom.api.channel;

import com.guflimc.brick.chat.api.channel.AbstractPermissionChatChannel;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

public class MinestomChatChannel extends AbstractPermissionChatChannel<Player> {

    public MinestomChatChannel(String name, String activator, Component format) {
        super(name, activator, format);
    }

    @Override
    public boolean canRead(Player player) {
        if ( action == RestrictedAction.READ_AND_TALK ) {
            return player.hasPermission(permission) || player.getPermissionLevel() == 4;
        }
        return true;
    }

    @Override
    public boolean canTalk(Player player) {
        if ( action == RestrictedAction.TALK ) {
            return player.hasPermission(permission) || player.getPermissionLevel() == 4;
        }
        return canRead(player);
    }

}
