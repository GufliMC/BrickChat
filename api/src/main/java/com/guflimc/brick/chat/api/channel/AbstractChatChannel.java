package com.guflimc.brick.chat.api.channel;

import net.kyori.adventure.text.Component;

public abstract class AbstractChatChannel<T> implements ChatChannel<T> {

    protected final String name;
    protected final String activator;
    protected final Component format;

    public AbstractChatChannel(String name, String activator, Component format) {
        this.name = name;
        this.activator = activator;
        this.format = format;
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
