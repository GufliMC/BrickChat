package com.guflimc.brick.chat.spigot.api;

import org.jetbrains.annotations.ApiStatus;

public class SpigotChatAPI {

    private SpigotChatAPI() {}

    private static SpigotChatManager chatManager;

    @ApiStatus.Internal
    public static void registerChatManager(SpigotChatManager manager) {
        chatManager = manager;
    }

    //

    /**
     * Get the registered chat manager.
     * @return the chat manager
     */
    public static SpigotChatManager get() {
        return chatManager;
    }

}
