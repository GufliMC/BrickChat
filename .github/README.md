# BrickChat

A simple Minecraft plugin/extension for creating chat channels

## Install

Get the [release](https://github.com/GufliMC/BrickChat/releases) and place it in your server.

### Config

You can change the settings in the `config.json`.

You can remove and create chat channels.
```json
{
  "channels": {
    "default": {
      "format": "{playername} > {chatmessage}"
    },
    "admin": {
      "format": "[ADMINCHAT] {playername} > {chatmessage}",
      "activator": "!",
      "protect": {
        "type": "READ",
        "permission": "brickchat.channel.admin"
      }
    }
  }
}
```

The **activator** is the symbol you start your message with to talk in this channel.

Available **protect** types are: 
* TALK: You can read, but need permission to talk
* READ_AND_TALK: You need permission to read and talk

### API

#### Gradle
```
repositories {
    maven { url "https://repo.jorisg.com/snapshots" }
}

dependencies {
    // minestom
    compileOnly 'com.guflimc.brick.chat:minestom-api:+'
    
    // spigot
    compileOnly 'com.guflimc.brick.chat:spigot-api:+'
}
```

#### Javadocs

Check the javadocs for all platforms [here](https://guflimc.github.io/BrickChat/).

#### Examples

```java
SpigotChatAPI.get().registerChatChannel(new SimpleChatChannel("trade", "$", "[TRADE] {playername} > {chatmessage}"));

SpigotChatChannel channel = ChatAPI.get().channelByName("trade");
SpigotChatAPI.get().send(channel, "hello fellow traders!");
```

```
@EventHandler
public void onChat(PlayerChannelChatEvent e) {
    System.out.println(e.chatChannel().name() + ": " + e.player() + " > " + e.message());
}
```
