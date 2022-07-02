package com.glyart.authmevelocity.proxy;

import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.listener.FastLoginListener;
import com.glyart.authmevelocity.proxy.listener.PluginMessageListener;
import com.glyart.authmevelocity.proxy.listener.ProxyListener;
import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthMeVelocityPlugin {
    public static final ChannelIdentifier AUTHMEVELOCITY_CHANNEL
        = MinecraftChannelIdentifier.create("authmevelocity", "main");
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path pluginDirectory;
    private AuthmeVelocityAPI api;

    protected final Set<UUID> loggedPlayers = Collections.<UUID>synchronizedSet(new HashSet<>());

    @Inject
    public AuthMeVelocityPlugin(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.pluginDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Toml toml = this.loadConfig(pluginDirectory);
        if (toml == null) {
            logger.warn("Failed to load config.toml. Shutting down.");
            return;
        }
        AuthMeConfig config = new AuthMeConfig(toml);
        this.api = new AuthmeVelocityAPI(this, config);
        proxy.getChannelRegistrar().register(AUTHMEVELOCITY_CHANNEL);
        proxy.getEventManager().register(this, new ProxyListener(config, api, logger, proxy));
        proxy.getEventManager().register(this, new PluginMessageListener(proxy, logger, config, api));

        if(proxy.getPluginManager().isLoaded("fastlogin")){
            proxy.getEventManager().register(this, new FastLoginListener(proxy, api));
        }

        if(proxy.getPluginManager().isLoaded("miniplaceholders")){
            AuthmePlaceholders.getExpansion(this).register();
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

    private Toml loadConfig(Path path){
        if(!Files.exists(path)){
            try {
                Files.createDirectory(path);
            } catch(IOException e){
                configError(e);
                return null;
            }
        }
        Path configPath = path.resolve("config.toml");
        if(!Files.exists(configPath)){
            try(InputStream in = this.getClass().getClassLoader().getResourceAsStream("config.toml")){
                Files.copy(in, configPath);
            } catch(IOException e){
                configError(e);
                return null;
            }
        }
        try {
            return new Toml().read(Files.newInputStream(configPath));
        } catch(IOException e){
            configError(e);
            return null;
        }
    }

    private void configError(Exception ex){
        logger.info("An error ocurred on configuration initialization: {}", ex.getMessage());
    }
}
