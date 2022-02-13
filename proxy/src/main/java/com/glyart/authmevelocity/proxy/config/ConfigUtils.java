package com.glyart.authmevelocity.proxy.config;

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ConfigUtils {
    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().character('&').hexColors().build();
    public static void sendBlockedMessage(Player player, AuthMeConfig config){
        String blockedMessage = config.getCommandsConfig().getBlockedMessage();
        if(!blockedMessage.isBlank()){
            player.sendMessage(SERIALIZER.deserialize(blockedMessage));
        }
    }
    private ConfigUtils(){}
}
