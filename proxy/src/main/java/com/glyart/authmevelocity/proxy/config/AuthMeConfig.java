package com.glyart.authmevelocity.proxy.config;

import java.util.List;

import com.glyart.authmevelocity.proxy.AuthMeVelocityPlugin;

import de.leonhard.storage.Yaml;

public interface AuthMeConfig {
    public static void defaultConfig(){
        Yaml config = AuthMeVelocityPlugin.getConfig();
        config.setDefault(
            "authservers",
            List.of(
                "auth1",
                "auth2"
            ));
        config.setDefault(
            "teleport.send-to-server-after-login",
            false);
        config.setDefault(
            "teleport.servers",
            List.of(
                "lobby1",
                "lobby2"
            ));
    }
}
