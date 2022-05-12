package com.guflimc.brick.chat.api.channel;

import net.kyori.adventure.text.Component;

public interface ChatChannel<T> {

    String name();

    Component format();

    String activator();

    boolean canRead(T entity);

    boolean canTalk(T entity);

}
