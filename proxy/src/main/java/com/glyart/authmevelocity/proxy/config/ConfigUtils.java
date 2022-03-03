package com.glyart.authmevelocity.proxy.config;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ConfigUtils {
    /**
     * Legacy Serializer
     * @deprecated use {@link ConfigUtils#MINIMESSAGE}
     */
    @Deprecated(forRemoval = true)
    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
        .character('&').hexColors().hexCharacter('#').build();
    public static final MiniMessage MINIMESSAGE = MiniMessage.miniMessage();

    public static void sendBlockedMessage(Player player, AuthMeConfig config){
        String blockedMessage = config.getCommandsConfig().getBlockedMessage();
        if(!blockedMessage.isBlank()){
            player.sendMessage(blockedMessage.indexOf('&') != -1
                ? SERIALIZER.deserialize(blockedMessage)
                : MINIMESSAGE.deserialize(blockedMessage)
            );
        }
    }

    @SuppressWarnings("unchecked")
    static <T>T getOrElse(Toml config, String key, T defaultValue){
        Toml configTable = config.getTable(key);
        return configTable == null ? defaultValue : (T)configTable.to(defaultValue.getClass());
    }
    private ConfigUtils(){}
}
