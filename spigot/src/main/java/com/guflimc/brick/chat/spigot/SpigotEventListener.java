package com.guflimc.brick.chat.spigot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SpigotEventListener implements Listener {

    private final SpigotBrickChatManager chatManager;

    public SpigotEventListener(SpigotBrickChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        chatManager.execute(event);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        chatManager.unsetDefaultChannel(event.getPlayer());
    }
}
