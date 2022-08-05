package me.adrianed.authmevelocity.velocity;

import me.adrianed.authmevelocity.velocity.commands.AuthmeCommand;
import me.adrianed.authmevelocity.velocity.config.AuthMeConfig;
import me.adrianed.authmevelocity.velocity.listener.ConnectListener;
import me.adrianed.authmevelocity.velocity.listener.FastLoginListener;
import me.adrianed.authmevelocity.velocity.listener.PluginMessageListener;
import me.adrianed.authmevelocity.velocity.listener.ProxyListener;
import me.adrianed.authmevelocity.api.velocity.AuthMeVelocityAPI;
import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import net.kyori.adventure.text.minimessage.MiniMessage;

import org.slf4j.Logger;

import java.util.function.Predicate;
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

@Plugin(
    id = "authmevelocity",
    name = "AuthMeVelocity",
    url = "https://github.com/4drian3d/AuthMeVelocity",
    description = "This plugin adds the support for AuthMeReloaded to Velocity",
    dependencies = {
        @Dependency(
            id = "miniplaceholders",
            optional = true
        ),
        @Dependency(
            id = "fastlogin",
            optional = true
        )
    }
)
public final class AuthMeVelocityPlugin implements AuthMeVelocityAPI {
    public static final ChannelIdentifier AUTHMEVELOCITY_CHANNEL
        = MinecraftChannelIdentifier.create("authmevelocity", "main");
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path pluginDirectory;
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
            AuthMePlaceholders.getExpansion(this).register();
        }

        AuthmeCommand.register(this, proxy.getCommandManager());

        this.sendInfoMessage();
    }

    protected ProxyServer getProxy(){
        return this.proxy;
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

        listeners.forEach(listener -> proxy.getEventManager().unregisterListener(this, listener));
        listeners.clear();

        listeners.add(new ProxyListener(config, this));
        listeners.add(new ConnectListener(config, this, proxy, logger));
        listeners.add(new PluginMessageListener(proxy, logger, config, this));

        if (proxy.getPluginManager().isLoaded("fastlogin")) {
            listeners.add(new FastLoginListener(proxy, this));
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

    @Override
    public boolean isLogged(Player player){
        return loggedPlayers.contains(player.getUniqueId());
    }

    @Override
    public boolean isNotLogged(Player player){
        return !loggedPlayers.contains(player.getUniqueId());
    }

    @Override
    public boolean addPlayer(Player player){
        return loggedPlayers.add(player.getUniqueId());
    }

    @Override
    public boolean removePlayer(Player player){
        return loggedPlayers.remove(player.getUniqueId());
    }

    @Override
    public void removePlayerIf(Predicate<Player> predicate){
        loggedPlayers.removeIf(uuid -> predicate.test(getProxy().getPlayer(uuid).orElse(null)));
    }

    @Override
    public boolean isInAuthServer(Player player){
        return player.getCurrentServer().map(this::isAuthServer).orElse(false);
    }

    @Override
    public boolean isAuthServer(RegisteredServer server){
        return config.getAuthServers().contains(server.getServerInfo().getName());
    }

    @Override
    public boolean isAuthServer(ServerConnection connection){
        return config.getAuthServers().contains(connection.getServerInfo().getName());
    }

    @Override
    public boolean isAuthServer(String server){
        return config.getAuthServers().contains(server);
    }
}
