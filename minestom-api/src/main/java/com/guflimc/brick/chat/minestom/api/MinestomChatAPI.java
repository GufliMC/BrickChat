package com.guflimc.brick.chat.minestom.api;

import org.jetbrains.annotations.ApiStatus;

public class MinestomChatAPI {

    private MinestomChatAPI() {}

    private static MinestomChatManager chatManager;

    @ApiStatus.Internal
    public static void registerChatManager(MinestomChatManager manager) {
        chatManager = manager;
    }

    //

    /**
     * Get the registered chat manager.
     * @return the chat manager
     */
    public static MinestomChatManager get() {
        return chatManager;
    }

}
