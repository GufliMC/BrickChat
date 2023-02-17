package com.guflimc.brick.chat.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guflimc.brick.chat.api.ChatManager;
import com.guflimc.brick.chat.api.channel.AbstractChatChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class ConfigParser<T> {

    private final ChatManager<T> chatManager;

    public ConfigParser(ChatManager<T> chatManager) {
        this.chatManager = chatManager;
    }

    protected abstract AbstractChatChannel<T> createChannel(String name, String activator, Component format, boolean restricted);

    public void parse(InputStream resource) {
        try (
                InputStreamReader isr = new InputStreamReader(resource);
        ) {
            JsonObject config = JsonParser.parseReader(isr).getAsJsonObject();
            JsonObject channels = config.get("channels").getAsJsonObject();
            for (String name : channels.keySet()) {
                JsonObject channel = channels.get(name).getAsJsonObject();
                String format = channel.get("format").getAsString();
                if (format == null) {
                    continue;
                }

                String activator = "";
                if (channel.has("activator")) {
                    activator = channel.get("activator").getAsString();
                }

                boolean restricted = channel.has("restricted") && channel.get("restricted").getAsBoolean();

                AbstractChatChannel<T> scc = createChannel(name, activator, MiniMessage.miniMessage().deserialize(format), restricted);
                chatManager.registerChatChannel(scc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
