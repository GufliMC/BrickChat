package com.guflimc.brick.chat.api.channel;

import net.kyori.adventure.text.Component;

public abstract class AbstractChatChannel<T> implements ChatChannel<T> {

    protected final String name;
    protected final String activator;
    protected final Component format;
    protected final boolean restricted;

    public AbstractChatChannel(String name, String activator, Component format, boolean restricted) {
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
