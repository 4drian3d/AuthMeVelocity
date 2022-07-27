package com.glyart.authmevelocity.proxy;

import com.glyart.authmevelocity.proxy.commands.AuthmeCommand;
import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.listener.FastLoginListener;
import com.glyart.authmevelocity.proxy.listener.PluginMessageListener;
import com.glyart.authmevelocity.proxy.listener.ProxyListener;
import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import net.kyori.adventure.text.minimessage.MiniMessage;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class AuthMeVelocityPlugin {
    public static final ChannelIdentifier AUTHMEVELOCITY_CHANNEL
        = MinecraftChannelIdentifier.create("authmevelocity", "main");
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path pluginDirectory;
    private AuthmeVelocityAPI api;
    private AuthMeConfig config;

    private final List<Object> listeners = new ArrayList<>(3);
    protected final Set<UUID> loggedPlayers = Collections.synchronizedSet(new HashSet<>());

    @Inject
    public AuthMeVelocityPlugin(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.pluginDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        if (!this.reload()) {
            logger.warn("Failed to load config.toml. Shutting down.");
            return;
        }

        proxy.getChannelRegistrar().register(AUTHMEVELOCITY_CHANNEL);

        if (proxy.getPluginManager().isLoaded("miniplaceholders")) {
            AuthmePlaceholders.getExpansion(this).register();
        }

        AuthmeCommand.register(this, proxy.getCommandManager());

        this.sendInfoMessage();
    }

    protected ProxyServer getProxy(){
        return this.proxy;
    }

    public AuthmeVelocityAPI getAPI(){
        return this.api;
    }

    private Toml loadConfig(Path path){
        try {
            if (Files.notExists(path)) {
                Files.createDirectory(path);
            }

            Path configPath = path.resolve("config.toml");
            if (Files.notExists(configPath)) {
                try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("config.toml")) {
                    Files.copy(Objects.requireNonNull(in, "The configuration does not exists"), configPath);
                }
            }

            return new Toml().read(Files.newInputStream(configPath));
        } catch (IOException ex) {
            logger.error("An error ocurred on configuration initialization", ex);
            return null;
        } catch(IllegalStateException ex) {
            logger.error("Invalid configuration provided", ex);
            return null;
        }
    }

    public boolean reload() {
        Toml toml = this.loadConfig(pluginDirectory);
        if (toml == null) {
            return false;
        }

        this.config = new AuthMeConfig(toml);
        this.api = new AuthmeVelocityAPI(this, config);

        listeners.forEach(listener -> proxy.getEventManager().unregisterListener(this, listener));
        listeners.clear();

        listeners.add(new ProxyListener(config, api, logger, proxy));
        listeners.add(new PluginMessageListener(proxy, logger, config, api));

        if (proxy.getPluginManager().isLoaded("fastlogin")) {
            listeners.add(new FastLoginListener(proxy, api));
        }

        listeners.forEach(listener -> proxy.getEventManager().register(this, listener));
        return true;
    }

    public void sendInfoMessage() {
        CommandSource source = proxy.getConsoleCommandSource();
        source.sendMessage(MiniMessage.miniMessage().deserialize(
            " <gray>--- <aqua>AuthMeVelocity</aqua> ---"));
        source.sendMessage(MiniMessage.miniMessage().deserialize(
            "<gray>AuthServers: <green>" + config.getAuthServers()));
        if (config.getToServerOptions().sendToServer()) {
            source.sendMessage(MiniMessage.miniMessage().deserialize(
                "<gray>LobbyServers: <green>" + config.getToServerOptions().getTeleportServers()));
        }
    }
}
