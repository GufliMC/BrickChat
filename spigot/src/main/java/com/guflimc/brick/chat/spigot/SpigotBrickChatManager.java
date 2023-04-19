package com.guflimc.brick.chat.spigot;

import com.guflimc.adventure.MixedLegacyComponentSerializer;
import com.guflimc.brick.chat.api.channel.ChatChannel;
import com.guflimc.brick.chat.common.BrickChatManager;
import com.guflimc.brick.chat.spigot.api.SpigotChatManager;
import com.guflimc.brick.chat.spigot.api.event.SpigotPlayerChannelChatEvent;
import com.guflimc.brick.placeholders.api.resolver.PlaceholderResolveContext;
import com.guflimc.brick.placeholders.spigot.api.SpigotPlaceholderAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SpigotBrickChatManager extends BrickChatManager<Player, SpigotPlayerChannelChatEvent> implements SpigotChatManager {

    private final Map<Player, String> defaultChannels = new ConcurrentHashMap<>();
    private final BukkitAudiences adventure;

    public SpigotBrickChatManager(BukkitAudiences adventure) {
        this.adventure = adventure;
    }

    void execute(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        SpigotPlayerChannelChatEvent channelChatEvent = dispatch(player, event.getMessage());
        if (channelChatEvent == null) {
            return; // operation was sent, not actual chat
        }

        // update spigot event
        event.setMessage(channelChatEvent.message());
        event.getRecipients().clear();
        event.getRecipients().addAll(channelChatEvent.recipients());

        // send messages
        send(event.getRecipients(), player, channelChatEvent.format(), event.getMessage());

//        String strFormat = LegacyComponentSerializer.legacySection().serialize(format);
//        event.setFormat(strFormat);
    }

    @Override
    public void send(ChatChannel<Player> channel, Player player, String message) {
        send(Bukkit.getOnlinePlayers().stream()
                        .filter(channel::canRead).collect(Collectors.toSet()),
                player, channel.format(), message);
    }

    @Override
    public Optional<ChatChannel<Player>> defaultChannel(Player entity) {
        return Optional.ofNullable(defaultChannels.get(entity)).flatMap(this::channelByName);
    }

    @Override
    public void setDefaultChannel(Player entity, ChatChannel<Player> channel) {
        defaultChannels.put(entity, channel.name());
    }

    @Override
    public void unsetDefaultChannel(Player entity) {
        defaultChannels.remove(entity);
    }

    @Override
    public void send(ChatChannel<Player> channel, Component text) {
        Bukkit.getOnlinePlayers().stream()
                .filter(channel::canRead)
                .forEach(p -> adventure.player(p).sendMessage(text));
    }

    @Override
    protected void send(Collection<Player> recipients, Player player, Component format, String message) {
        Component result = format(format, player, message);

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("BrickPlaceholders")) {
            recipients.forEach(r -> {
                Component f = SpigotPlaceholderAPI.get().replace(result, PlaceholderResolveContext.of(player, r));
                adventure.player(r).sendMessage(f);
            });
            return;
        }

        recipients.forEach(r -> adventure.player(r).sendMessage(result));
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

    protected SpigotPlayerChannelChatEvent dispatch(ChatChannel<Player> channel, Player player, String message) {
        Set<Player> receivers = Bukkit.getOnlinePlayers().stream()
                .filter(channel::canRead).collect(Collectors.toSet());

        SpigotPlayerChannelChatEvent event = new SpigotPlayerChannelChatEvent(player, channel, message, receivers, channel.format());
        Bukkit.getPluginManager().callEvent(event);

        return event;
    }
}
