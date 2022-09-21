package com.guflimc.brick.chat.spigot.api.event;

import com.guflimc.brick.chat.api.channel.ChatChannel;
import com.guflimc.brick.chat.api.event.PlayerChannelChatEvent;
import com.guflimc.brick.chat.api.event.SimplePlayerChannelChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class SpigotPlayerChannelChatEvent extends Event implements PlayerChannelChatEvent<Player>, Cancellable {

    private final SimplePlayerChannelChatEvent<Player> wrapped;

    public SpigotPlayerChannelChatEvent(Player entity, ChatChannel<Player> chatChannel, String message, Set<Player> recipients, Component format) {
        this.wrapped = new SimplePlayerChannelChatEvent<>(entity, chatChannel, message, recipients, format);
    }

    public ChatChannel<Player> chatChannel() {
        return wrapped.chatChannel();
    }

    public String message() {
        return wrapped.message();
    }

    public Component format() {
        return wrapped.format();
    }

    public Set<Player> recipients() {
        return wrapped.recipients();
    }

    public void setFormat(Component format) {
        this.wrapped.setFormat(format);
    }

    public Player player() {
        return wrapped.player();
    }

    public boolean isCancelled() {
        return wrapped.isCancelled();
    }

    public void setCancelled(boolean cancelled) {
        wrapped.setCancelled(cancelled);
    }

    // HANDLERS
    private static final HandlerList handlers = new HandlerList();
    @NotNull @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
