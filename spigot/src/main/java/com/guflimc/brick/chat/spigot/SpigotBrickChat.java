package com.guflimc.brick.chat.spigot;

import com.guflimc.brick.chat.spigot.api.SpigotChatAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SpigotBrickChat extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Enabling " + nameAndVersion() + ".");

        BukkitAudiences adventure = BukkitAudiences.create(this);

        // register chat manager
        SpigotBrickChatManager chatManager = new SpigotBrickChatManager(adventure);
        SpigotChatAPI.registerChatManager(chatManager);

        // register events
        getServer().getPluginManager().registerEvents(new SpigotEventListener(chatManager), this);

        // load channels
        SpigotConfigParser parser = new SpigotConfigParser(chatManager);
        saveResource("config.json", false);
        try (
                InputStream is = new FileInputStream(new File(getDataFolder(), "config.json"))
        ) {
            parser.parse(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
