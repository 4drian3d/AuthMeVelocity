package com.glyart.authmevelocity.proxy;

import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.listener.FastLoginListener;
import com.glyart.authmevelocity.proxy.listener.ProxyListener;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

import de.leonhard.storage.Yaml;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthMeVelocityPlugin {
    private static ProxyServer proxy;
    private final Logger logger;
    private static Yaml config = new Yaml("config", "plugins/AuthmeVelocity");

    protected static final Set<UUID> loggedPlayers = Collections.synchronizedSet(new HashSet<UUID>());

    @Inject
    public AuthMeVelocityPlugin(ProxyServer server, Logger logger) {
        proxy = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        proxy.getChannelRegistrar().register(
            MinecraftChannelIdentifier.create("authmevelocity", "main"));
        proxy.getEventManager().register(this, new ProxyListener(proxy, logger));
        if(proxy.getPluginManager().getPlugin("fastlogin").isPresent()){
            proxy.getEventManager().register(this, new FastLoginListener(proxy));
        }
        AuthMeConfig.defaultConfig();
        logger.info("-- AuthMeVelocity enabled --");
        logger.info("AuthServers: " + config.getList("authservers"));
        if(config.getBoolean("teleport.send-to-server-after-login")){
            logger.info("LobbyServers: " + config.getList("teleport.servers"));
        }
    }

    public static Yaml getConfig(){
        return config;
    }

    protected static ProxyServer getProxy(){
        return proxy;
    }
}
