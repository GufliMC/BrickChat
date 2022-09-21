package com.guflimc.brick.chat.api.channel;

import net.kyori.adventure.text.Component;

public abstract class AbstractRestrictedChatChannel<T> extends AbstractChatChannel<T> {

    protected RestrictedAction action;

    public AbstractRestrictedChatChannel(String name, String activator, Component format) {
        super(name, activator, format);
    }

    public final void restrict(RestrictedAction action) {
        this.action = action;
    }

    public enum RestrictedAction {
        TALK, READ_AND_TALK;
    }
}
