package com.guflimc.brick.chat.common;

import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;
import com.electronwill.nightconfig.core.conversion.SpecNotNull;
import com.guflimc.adventure.MixedLegacyComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@PreserveNotNull
public class ChatConfig {

    public List<ChatChannelConfig> channels = new ArrayList<>();

    public ChatConfig() {
        channels.add(new ChatChannelConfig("global",
                MixedLegacyComponentSerializer.deserialize("<white>%player_name%<white>: <gray>%chatmessage%")));
        channels.add(new ChatChannelConfig("staffchat",
                MixedLegacyComponentSerializer.deserialize("<red>[SC] <white>%player_name%<white>: %chatmessage%"),
                "!", true));
    }

    @PreserveNotNull
    public static class ChatChannelConfig {

        @SpecNotNull
        public String name;

        @Conversion(ComponentConverter.class)
        @SpecNotNull
        public Component format;

        public String activator = "";
        public boolean restricted = false;

        private ChatChannelConfig() {
        }

        public ChatChannelConfig(@NotNull String name, @NotNull Component format, String activator, boolean restricted) {
            this.name = name;
            this.format = format;
            this.activator = activator;
            this.restricted = restricted;
        }

        public ChatChannelConfig(@NotNull String name, @NotNull Component format) {
            this(name, format, "", false);
        }

    }

    static class ComponentConverter implements Converter<Component, String> {

        @Override
        public Component convertToField(String value) {
            return MiniMessage.miniMessage().deserialize(value);
        }

        @Override
        public String convertFromField(Component value) {
            return MiniMessage.miniMessage().serialize(value);
        }
    }

}
