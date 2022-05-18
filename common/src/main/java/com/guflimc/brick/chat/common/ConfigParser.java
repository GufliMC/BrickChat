package com.guflimc.brick.chat.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guflimc.brick.chat.api.ChatManager;
import com.guflimc.brick.chat.api.channel.AbstractPermissionChatChannel;
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

    protected abstract AbstractPermissionChatChannel<T> createChannel(String name, String activator, Component format);

    public void parse(InputStream resource) {
        try (
                InputStreamReader isr = new InputStreamReader(resource);
        ) {
            JsonObject config = JsonParser.parseReader(isr).getAsJsonObject();
            JsonObject channels = config.get("channels").getAsJsonObject();
            for ( String name : channels.keySet() ) {
                JsonObject channel = channels.get(name).getAsJsonObject();
                String format = channel.get("format").getAsString();
                if ( format == null ) {
                    continue;
                }

                String activator = "";
                if ( channel.has("activator") ) {
                    activator = channel.get("activator").getAsString();
                }

                AbstractPermissionChatChannel<T> scc = createChannel(name, activator, MiniMessage.miniMessage().deserialize(format));
                chatManager.registerChatChannel(scc);

                if ( !channel.has("protect") ) {
                    continue;
                }

                JsonObject protect = channel.get("protect").getAsJsonObject();
                AbstractPermissionChatChannel.RestrictedAction action = AbstractPermissionChatChannel.RestrictedAction.READ_AND_TALK;
                if (protect.has("type")) {
                    action = AbstractPermissionChatChannel.RestrictedAction.valueOf(protect.get("type").getAsString()
                            .toUpperCase().replace("-", "_"));
                }

                if ( !protect.has("permission") ) {
                    continue;
                }

                scc.protect(action, protect.get("permission").getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
