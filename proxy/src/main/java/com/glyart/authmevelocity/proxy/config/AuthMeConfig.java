package com.glyart.authmevelocity.proxy.config;

import java.util.List;

import com.glyart.authmevelocity.proxy.AuthMeVelocityPlugin;

public interface AuthMeConfig {
    public static void defaultConfig(){
        AuthMeVelocityPlugin.getConfig().setDefault("authservers", List.of("auth1", "auth2"));
    }
}
