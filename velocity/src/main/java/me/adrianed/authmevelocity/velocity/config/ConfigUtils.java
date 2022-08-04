package me.adrianed.authmevelocity.velocity.config;

import java.util.List;
import java.util.function.Supplier;

import java.lang.reflect.ParameterizedType;

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
    static <T>T getObjectOrElse(Toml config, String key, Supplier<T> defaultValue){
        Toml configTable = config.getTable(key);
        return configTable == null ? defaultValue.get() : (T)configTable.to((Class<T>)((ParameterizedType)defaultValue.getClass()
            .getGenericInterfaces()[0]).getActualTypeArguments()[0]);
    }

    static <T>T getObjectOrElse(Toml config, String key, Class<T> clazz, Supplier<T> defaultValue){
        Toml configTable = config.getTable(key);
        return configTable == null ? defaultValue.get() : configTable.to(clazz);
    }

    static List<String> listOrElse(Toml config, String key, Supplier<List<String>> defaultList){
        List<String> list = config.getList(key);
        return list != null ? list : defaultList.get();
    }
    private ConfigUtils(){}
}
