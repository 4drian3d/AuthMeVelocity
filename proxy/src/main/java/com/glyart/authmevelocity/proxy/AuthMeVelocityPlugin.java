package com.glyart.authmevelocity.proxy;

import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.listener.FastLoginListener;
import com.glyart.authmevelocity.proxy.listener.ProxyListener;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

import de.leonhard.storage.Yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AuthMeVelocityPlugin {
    private static ProxyServer proxy;
    private final Logger logger;
    private static Yaml config = new Yaml("config", "plugins/AuthmeVelocity");

    protected static final List<UUID> loggedPlayers = Collections.synchronizedList(new ArrayList<>());

    @Inject
    public AuthMeVelocityPlugin(ProxyServer server, Logger logger) {
        proxy = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        proxy.getChannelRegistrar().register(
            new LegacyChannelIdentifier("authmevelocity:main"),
            MinecraftChannelIdentifier.create("authmevelocity", "main"));
        proxy.getEventManager().register(this, new ProxyListener(proxy));
        proxy.getEventManager().register(this, new FastLoginListener(proxy));
        AuthMeConfig.defaultConfig();
        logger.info("AuthMeVelocity enabled");
        logger.info("AuthServers: " + config.getList("authservers"));
    }

    public static Yaml getConfig(){
        return config;
    }

    protected static ProxyServer getProxy(){
        return proxy;
    }
}
