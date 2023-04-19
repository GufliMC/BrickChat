package com.guflimc.brick.chat.spigot;

import com.guflimc.brick.chat.common.ChatConfig;
import com.guflimc.brick.chat.spigot.api.SpigotChatAPI;
import com.guflimc.brick.chat.spigot.api.channel.SpigotChatChannel;
import com.guflimc.config.toml.TomlConfig;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class SpigotBrickChat extends JavaPlugin {

    @Override
    public void onEnable() {
        BukkitAudiences adventure = BukkitAudiences.create(this);

        // register chat manager
        SpigotBrickChatManager chatManager = new SpigotBrickChatManager(adventure);
        SpigotChatAPI.registerChatManager(chatManager);

        // register events
        getServer().getPluginManager().registerEvents(new SpigotEventListener(chatManager), this);

        // CONFIG
        ChatConfig config = TomlConfig.load(getDataFolder().toPath().resolve("config.toml"), new ChatConfig());
        config.channels.forEach(c ->
                chatManager.registerChatChannel(new SpigotChatChannel(c.name, c.format, c.activator, c.restricted)));

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getName() + " v" + getDescription().getVersion();
    }

}
