package com.glyart.authmevelocity.proxy.config;

import com.glyart.authmevelocity.proxy.config.AuthMeConfig.Config;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ConfigUtils {
    public static void sendBlockedMessage(Player player, Config config){
        String blockedMessage = config.getCommandsConfig().getBlockedMessage();
        if(!blockedMessage.isBlank()){
            player.sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize(
                    blockedMessage));
        }
    }
    private ConfigUtils(){}
}
