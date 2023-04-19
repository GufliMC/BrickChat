package com.guflimc.brick.chat.api.channel;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractChatChannel<T> implements ChatChannel<T> {

    protected final String name;
    protected final String activator;
    protected final Component format;
    protected final boolean restricted;

    public AbstractChatChannel(@NotNull String name, @NotNull Component format, @NotNull String activator, boolean restricted) {
        this.name = name;
        this.activator = activator;
        this.format = format;
        this.restricted = restricted;
    }

    @Override
    public final String name() {
        return name;
    }

    @Override
    public final Component format() {
        return format;
    }

    @Override
    public final String activator() {
        return activator;
    }

}
