package com.guflimc.brick.chat.minestom;

import com.guflimc.brick.chat.api.channel.ChatChannel;
import com.guflimc.brick.chat.common.BrickChatManager;
import com.guflimc.brick.chat.minestom.api.MinestomChatManager;
import com.guflimc.brick.chat.minestom.api.event.MinestomPlayerChannelChatEvent;
import com.guflimc.brick.placeholders.api.resolver.PlaceholderResolveContext;
import com.guflimc.brick.placeholders.minestom.api.MinestomPlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.tag.Tag;

import java.util.Collection;
import java.util.Optional;
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
    public Optional<ChatChannel<Player>> defaultChannel(Player entity) {
        if (!entity.hasTag(DEFAULT_CHANNEL_TAG)) {
            return Optional.empty();
        }

        String name = entity.getTag(DEFAULT_CHANNEL_TAG);
        return channelByName(name);
    }

    @Override
    public void setDefaultChannel(Player entity, ChatChannel<Player> channel) {
        entity.setTag(DEFAULT_CHANNEL_TAG, channel.name());
    }

    @Override
    public void unsetDefaultChannel(Player entity) {
        entity.removeTag(DEFAULT_CHANNEL_TAG);
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

        if (MinecraftServer.getExtensionManager().hasExtension("brickplaceholders")) {
            recipients.forEach(r -> {
                Component f = MinestomPlaceholderAPI.get().replace(result, PlaceholderResolveContext.of(player, r));
                r.sendMessage(f);
            });
        }

        recipients.forEach(p -> p.sendMessage(result));
    }

    @Override
    protected Component format(Component format, Player player, String msg) {
        Component message;
        if (player.hasPermission("brickchat.parse")) {
            message = MiniMessage.miniMessage().deserialize(msg);
        } else {
            message = Component.text(msg);
        }

        return super.format(format, player, message);
    }

    protected MinestomPlayerChannelChatEvent dispatch(ChatChannel<Player> channel, Player player, String message) {
        Set<Player> receivers = MinecraftServer.getConnectionManager().getOnlinePlayers().stream()
                .filter(channel::canRead).collect(Collectors.toSet());

        MinestomPlayerChannelChatEvent event = new MinestomPlayerChannelChatEvent(player, channel, message, receivers, channel.format());
        MinecraftServer.getGlobalEventHandler().call(event);

        return event;
    }
}
