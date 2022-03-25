package com.glyart.authmevelocity.proxy;

import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.listener.FastLoginListener;
import com.glyart.authmevelocity.proxy.listener.PluginMessageListener;
import com.glyart.authmevelocity.proxy.listener.ProxyListener;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.nicecodes.commands.HubCommand;
import com.nicecodes.objects.TomlFile;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.*;

public class AuthMeVelocityPlugin {
    private final ProxyServer proxy;
    private final Logger logger;

    private AuthmeVelocityAPI api;
    private AuthMeConfig config;

    private final HubCommand hubCommand;
    private final CommandManager commandManager;

    private final TomlFile configTomlFile;
    private final TomlFile hubTomlFile;


    protected final Set<UUID> loggedPlayers = Collections.synchronizedSet(new HashSet<>());

    @Inject
    public AuthMeVelocityPlugin(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;

        this.configTomlFile = new TomlFile(dataDirectory, "config.toml");
        this.hubTomlFile = new TomlFile(dataDirectory, "hub.toml");

        this.hubCommand = new HubCommand(this);
        this.commandManager = proxy.getCommandManager();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        config = new AuthMeConfig(configTomlFile.getToml());

        this.api = new AuthmeVelocityAPI(this, config);

        proxy.getChannelRegistrar().register(MinecraftChannelIdentifier.create("authmevelocity", "main"));

        proxy.getEventManager().register(this, new ProxyListener(config, api, logger, proxy));
        proxy.getEventManager().register(this, new PluginMessageListener(proxy, logger, config, api));

        loadCommand();
        registerExpansions();

        logger.info("-- AuthMeVelocity enabled --");
        logger.info("AuthServers: {}", config.getAuthServers());

        if (config.getToServerOptions().sendToServer()) {
            logger.info("LobbyServers: {}", config.getToServerOptions().getTeleportServers());
        }
    }

    private void registerExpansions() {
        if (proxy.getPluginManager().isLoaded("fastlogin")) {
            proxy.getEventManager().register(this, new FastLoginListener(proxy, api));
        }

        if (proxy.getPluginManager().isLoaded("miniplaceholders")) {
            AuthmePlaceholders.getExpansion(this).register();
        }
    }

    public AuthmeVelocityAPI getAPI() {
        return this.api;
    }

    public List<RegisteredServer> getServers() {
        List<String> stringList = config.getToServerOptions().getTeleportServers();
        List<RegisteredServer> registeredServers = Lists.newArrayList();

        for (String lobbyName : stringList) {
            proxy.getServer(lobbyName).ifPresent(registeredServers::add);
        }

        return registeredServers;
    }

    private void loadCommand() {
        CommandMeta commandMeta = commandManager.metaBuilder("hub").build();
        commandManager.register(commandMeta, hubCommand);
    }

    public Toml getHubToml() {
        return hubTomlFile.getToml();
    }

    public ProxyServer getProxy() {
        return this.proxy;
    }
}
