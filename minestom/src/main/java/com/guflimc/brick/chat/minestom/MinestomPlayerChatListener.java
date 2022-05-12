package com.guflimc.brick.chat.minestom;

import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class MinestomPlayerChatListener implements EventListener<PlayerChatEvent> {

    private final MinestomBrickChatManager chatManager;

    public MinestomPlayerChatListener(MinestomBrickChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Override
    public @NotNull Class<PlayerChatEvent> eventType() {
        return PlayerChatEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerChatEvent event) {
        chatManager.execute(event);
        return Result.SUCCESS;
    }
}
