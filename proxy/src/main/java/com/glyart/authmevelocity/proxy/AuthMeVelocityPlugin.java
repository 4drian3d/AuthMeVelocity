package com.glyart.authmevelocity.proxy;

import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.config.AuthMeConfig.Config;
import com.glyart.authmevelocity.proxy.listener.FastLoginListener;
import com.glyart.authmevelocity.proxy.listener.PluginMessageListener;
import com.glyart.authmevelocity.proxy.listener.ProxyListener;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class AuthMeVelocityPlugin {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path pluginDirectory;
    private final AuthmeVelocityAPI api;
    Config config = null;

    protected final Set<UUID> loggedPlayers = Collections.<UUID>synchronizedSet(new HashSet<>());

    @Inject
    public AuthMeVelocityPlugin(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.pluginDirectory = dataDirectory;
        this.api = new AuthmeVelocityAPI(this);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.config = Objects.requireNonNull(new AuthMeConfig().loadConfig(pluginDirectory, logger), "configuration cannot be null");

        proxy.getChannelRegistrar().register(MinecraftChannelIdentifier.create("authmevelocity", "main"));
        proxy.getEventManager().register(this, new ProxyListener(config, api));
        proxy.getEventManager().register(this, new PluginMessageListener(proxy, logger, config, api));

        if(proxy.getPluginManager().isLoaded("fastlogin")){
            proxy.getEventManager().register(this, new FastLoginListener(proxy, api));
        }

        logger.info("-- AuthMeVelocity enabled --");
        logger.info("AuthServers: {}", config.getAuthServers());
        if(config.getToServerOptions().sendToServer()){
            logger.info("LobbyServers: {}", config.getToServerOptions().getTeleportServers());
        }
    }

    protected ProxyServer getProxy(){
        return this.proxy;
    }

    public AuthmeVelocityAPI getAPI(){
        return this.api;
    }
}
