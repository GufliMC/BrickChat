package com.guflimc.brick.chat.minestom;

import com.guflimc.brick.chat.api.channel.ChatChannel;
import com.guflimc.brick.chat.common.BrickChatManager;
import com.guflimc.brick.chat.minestom.api.MinestomChatManager;
import com.guflimc.brick.chat.minestom.api.event.MinestomPlayerChannelChatEvent;
import com.guflimc.brick.placeholders.minestom.api.MinestomPlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.tag.Tag;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MinestomBrickChatManager extends BrickChatManager<Player, MinestomPlayerChannelChatEvent> implements MinestomChatManager {

    private final Tag<String> DEFAULT_CHANNEL_TAG = Tag.String("DEFAULT_CHAT_CHANNEL");

    void execute(PlayerChatEvent event) {
        Player player = event.getPlayer();

        MinestomPlayerChannelChatEvent channelChatEvent = dispatch(player, event.getMessage());
        if (channelChatEvent == null) {
            event.setCancelled(true);
            return;
        }

        // update minestom event
        event.setMessage(channelChatEvent.message());
        event.getRecipients().clear();
        event.getRecipients().addAll(channelChatEvent.recipients());
        event.setChatFormat(evt -> format(channelChatEvent.format(), player, event.getMessage()));
    }

    @Override
    public void send(ChatChannel<Player> channel, Player player, String message) {
        send(MinecraftServer.getConnectionManager().getOnlinePlayers().stream()
                        .filter(channel::canRead).collect(Collectors.toSet()),
                player, channel.format(), message);
    }

    @Override
    public void send(ChatChannel<Player> channel, Component text) {
        MinecraftServer.getConnectionManager().getOnlinePlayers().stream()
                .filter(channel::canRead)
                .forEach(p -> p.sendMessage(text));
    }

    @Override
    protected void send(Collection<Player> recipients, Player player, Component format, String message) {
        Component result = format(format, player, message);
        recipients.forEach(p -> p.sendMessage(result));
    }

    @Override
    protected Component format(Component format, Player player, String message) {
        Component result = super.format(format, player, message);

        result = format
                .replaceText(builder -> {
                    builder.match(Pattern.quote("{playername}"))
                            .replacement(player.getName());
                });

        if ( MinecraftServer.getExtensionManager().hasExtension("brickplaceholders") ) {
            result = MinestomPlaceholderAPI.get().replace(player, result);
        }

        return result;
    }

    @Override
    protected MinestomPlayerChannelChatEvent dispatch(Player player, String message) {
        message = message.trim();

        List<ChatChannel<Player>> channels = channels().stream()
                .filter(c -> c.canTalk(player))
                .sorted(Comparator.comparingInt(ch -> ch.activator() == null ? 0 : -ch.activator().length()))
                .toList();

        // initialize default channel
        ChatChannel<Player> channel = null;
        if (player.hasTag(DEFAULT_CHANNEL_TAG)) {
            String name = player.getTag(DEFAULT_CHANNEL_TAG);
            channel = channelByName(name);
        }

        // unset default chat channel by just typing the prefix
        if (channel != null && channel.activator() != null && channel.activator().equals(message)) {
            player.removeTag(DEFAULT_CHANNEL_TAG);
            return null;
        }

        // set default chat channel by just typing the prefix
        for (ChatChannel<Player> ch : channels) {
            if (ch.activator() != null && !ch.activator().equals("") && ch.activator().equals(message)) {
                player.setTag(DEFAULT_CHANNEL_TAG, ch.name());
                return null;
            }
        }

        return super.dispatch(player, message, channel);
    }

    protected MinestomPlayerChannelChatEvent dispatch(ChatChannel<Player> channel, Player player, String message) {
        Set<Player> receivers = MinecraftServer.getConnectionManager().getOnlinePlayers().stream()
                .filter(channel::canRead).collect(Collectors.toSet());

        MinestomPlayerChannelChatEvent event = new MinestomPlayerChannelChatEvent(player, channel, message, receivers, channel.format());
        MinecraftServer.getGlobalEventHandler().call(event);

        return event;
    }
}
