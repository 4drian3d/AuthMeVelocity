package com.glyart.authmevelocity.proxy;

import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.listener.ProxyListener;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Plugin(id = "authmevelocity", name = "AuthMeVelocity", version = "1.0.0", url = "https://github.com/Glyart/AuthMeVelocity", description = "This plugin adds the support for AuthMeReloaded to Velocity.", authors = {"xQuickGlare"})
@Getter
public class AuthMeVelocityPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataFolder;

    private final List<UUID> loggedPlayers = Collections.synchronizedList(new ArrayList<>());
    
    private AuthMeConfig config;

    @Inject
    public AuthMeVelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
    }
    
    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        try {
            config = AuthMeConfig.loadConfig(dataFolder);
        } catch (IOException e) {
            logger.error("An error occurred while enabling AuthMeVelocity.", e);
            return;
        }
        
        server.getChannelRegistrar().register(new LegacyChannelIdentifier("authmevelocity:main"), MinecraftChannelIdentifier.create("authmevelocity", "main"));
        server.getEventManager().register(this, new ProxyListener(this));
        logger.info("AuthMeVelocity enabled.");
    }
    
}
