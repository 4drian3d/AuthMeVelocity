package com.glyart.authmevelocity.proxy.config;

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ConfigUtils {
    public static void sendBlockedMessage(Player player, AuthMeConfig config){
        String blockedMessage = config.getCommandsConfig().getBlockedMessage();
        if(!blockedMessage.isBlank()){
            player.sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize(
                    blockedMessage));
        }
    }
    private ConfigUtils(){}
}
