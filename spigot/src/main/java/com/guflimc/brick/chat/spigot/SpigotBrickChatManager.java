package com.guflimc.brick.chat.spigot;

import com.guflimc.brick.chat.api.channel.ChatChannel;
import com.guflimc.brick.chat.common.BrickChatManager;
import com.guflimc.brick.chat.spigot.api.SpigotChatManager;
import com.guflimc.brick.chat.spigot.api.event.SpigotPlayerChannelChatEvent;
import com.guflimc.brick.placeholders.spigot.api.SpigotPlaceholderAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpigotBrickChatManager extends BrickChatManager<Player, SpigotPlayerChannelChatEvent> implements SpigotChatManager {

    private final Map<Player, String> defaultChannels = new HashMap<>();

    private final BukkitAudiences adventure;
    public SpigotBrickChatManager(BukkitAudiences adventure) {
        this.adventure = adventure;
    }


    void quit(Player player) {
        defaultChannels.remove(player);
    }

    void execute(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        SpigotPlayerChannelChatEvent channelChatEvent = dispatch(player, event.getMessage());
        if (channelChatEvent == null) {
            event.setCancelled(true);
            return;
        }

        // update spigot event
        event.setMessage(channelChatEvent.message());
        event.getRecipients().clear();
        event.getRecipients().addAll(channelChatEvent.recipients());

        Component format = format(channelChatEvent.format(), player, event.getMessage());
        String strFormat = LegacyComponentSerializer.legacySection().serialize(format);
        event.setFormat(strFormat);
    }

    @Override
    public void send(ChatChannel<Player> channel, Player player, String message) {
        send(Bukkit.getOnlinePlayers().stream()
                        .filter(channel::canRead).collect(Collectors.toSet()),
                player, channel.format(), message);
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
        recipients.forEach(p -> adventure.player(p).sendMessage(result));
    }

    @Override
    protected Component format(Component format, Player player, String message) {
        Component result = super.format(format, player, message);

        result = format
                .replaceText(builder -> {
                    builder.match(Pattern.quote("{playername}"))
                            .replacement(player.getName());
                });

        if ( Bukkit.getServer().getPluginManager().isPluginEnabled("BrickPlaceholders") ) {
            result = SpigotPlaceholderAPI.get().replace(player, result);
        }

        return result;
    }

    @Override
    protected SpigotPlayerChannelChatEvent dispatch(Player player, String message) {
        message = message.trim();

        List<ChatChannel<Player>> channels = channels().stream()
                .filter(c -> c.canTalk(player))
                .sorted(Comparator.comparingInt(ch -> ch.activator() == null ? 0 : -ch.activator().length()))
                .toList();

        // initialize default channel
        ChatChannel<Player> channel = null;
        if ( defaultChannels.containsKey(player) ) {
            String name = defaultChannels.get(player);
            channel = channelByName(name);
        }

        // unset default chat channel by just typing the prefix
        if (channel != null && channel.activator() != null && channel.activator().equals(message)) {
            defaultChannels.remove(player);
            return null;
        }

        // set default chat channel by just typing the prefix
        for (ChatChannel<Player> ch : channels) {
            if (ch.activator() != null && !ch.activator().equals("") && ch.activator().equals(message)) {
                defaultChannels.put(player, ch.name());
                return null;
            }
        }

        return super.dispatch(player, message, channel);
    }

    protected SpigotPlayerChannelChatEvent dispatch(ChatChannel<Player> channel, Player player, String message) {
        Set<Player> receivers = Bukkit.getOnlinePlayers().stream()
                .filter(channel::canRead).collect(Collectors.toSet());

        SpigotPlayerChannelChatEvent event = new SpigotPlayerChannelChatEvent(player, channel, message, receivers, channel.format());
        Bukkit.getPluginManager().callEvent(event);

        return event;
    }
}
