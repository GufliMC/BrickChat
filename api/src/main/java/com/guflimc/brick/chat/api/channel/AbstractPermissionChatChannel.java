package com.guflimc.brick.chat.api.channel;

import net.kyori.adventure.text.Component;

public abstract class AbstractPermissionChatChannel<T> extends AbstractChatChannel<T> {

    protected RestrictedAction action;
    protected String permission;

    public AbstractPermissionChatChannel(String name, String activator, Component format) {
        super(name, activator, format);
    }

    public final void protect(RestrictedAction action, String permission) {
        this.action = action;
        this.permission = permission;
    }

    public enum RestrictedAction {
        TALK, READ_AND_TALK;
    }
}
