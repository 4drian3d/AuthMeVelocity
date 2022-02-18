package com.glyart.authmevelocity.proxy.config;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ConfigUtils {
    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
        .character('&').hexColors().hexCharacter('#').build();

    public static void sendBlockedMessage(Player player, AuthMeConfig config){
        String blockedMessage = config.getCommandsConfig().getBlockedMessage();
        if(!blockedMessage.isBlank()){
            player.sendMessage(SERIALIZER.deserialize(blockedMessage));
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends Object>T getOrElse(Toml config, String key, T defaultValue){
        Toml configTable = config.getTable(key);
        return configTable == null ? defaultValue : (T)configTable.to(defaultValue.getClass());
    }
    private ConfigUtils(){}
}
