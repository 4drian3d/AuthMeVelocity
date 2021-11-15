package com.glyart.authmevelocity.proxy;

import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.listener.FastLoginListener;
import com.glyart.authmevelocity.proxy.listener.ProxyListener;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthMeVelocityPlugin {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path pluginDirectory;
    private static AuthMeVelocityPlugin plugin;

    protected static final Set<UUID> loggedPlayers = Collections.synchronizedSet(new HashSet<UUID>());

    @Inject
    public AuthMeVelocityPlugin(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        plugin = this;
        this.proxy = proxy;
        this.logger = logger;
        this.pluginDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        AuthMeConfig.loadConfig(pluginDirectory, logger);
        @NotNull var config = AuthMeConfig.getConfig();

        proxy.getChannelRegistrar().register(
            MinecraftChannelIdentifier.create("authmevelocity", "main"));
        proxy.getEventManager().register(this, new ProxyListener(proxy, logger, config));
        proxy.getPluginManager().getPlugin("fastlogin").ifPresent(fastlogin ->
            proxy.getEventManager().register(this, new FastLoginListener(proxy)));

        logger.info("-- AuthMeVelocity enabled --");
        logger.info("AuthServers: {}", config.getAuthServers());
        if(config.getToServerOptions().sendToServer()){
            logger.info("LobbyServers: {}", config.getToServerOptions().getTeleportServers());
        }
    }

    protected ProxyServer getProxy(){
        return this.proxy;
    }

    public static AuthMeVelocityPlugin getInstance(){
        return plugin;
    }
}
