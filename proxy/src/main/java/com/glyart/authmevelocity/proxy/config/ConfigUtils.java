package com.glyart.authmevelocity.proxy.config;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;

public final class ConfigUtils {
    public static final MiniMessage MINIMESSAGE = MiniMessage.miniMessage();

    public static void sendBlockedMessage(Player player, AuthMeConfig config){
        String blockedMessage = config.getCommandsConfig().getBlockedMessage();
        if(!blockedMessage.isBlank()){
            player.sendMessage(MINIMESSAGE.deserialize(blockedMessage));
        }
    }

    @SuppressWarnings("unchecked")
    static <T>T getOrElse(Toml config, String key, T defaultValue){
        Toml configTable = config.getTable(key);
        return configTable == null ? defaultValue : (T)configTable.to(defaultValue.getClass());
    }
    private ConfigUtils(){}
}
