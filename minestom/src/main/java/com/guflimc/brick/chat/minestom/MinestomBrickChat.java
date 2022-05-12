package com.guflimc.brick.chat.minestom;

import com.guflimc.brick.chat.minestom.api.MinestomChatAPI;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;

import java.io.IOException;
import java.io.InputStream;

public class MinestomBrickChat extends Extension {

    @Override
    public void initialize() {
        getLogger().info("Enabling " + nameAndVersion() + ".");

        MinestomBrickChatManager chatManager = new MinestomBrickChatManager();
        MinestomChatAPI.registerChatManager(chatManager);

        MinecraftServer.getGlobalEventHandler()
                .addListener(new MinestomPlayerChatListener(chatManager));

        // load channels
        MinestomConfigParser parser = new MinestomConfigParser(chatManager);
        try (
                InputStream is = getResource("config.json");
        ) {
            parser.parse(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void terminate() {
        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getOrigin().getName() + " v" + getOrigin().getVersion();
    }

}
