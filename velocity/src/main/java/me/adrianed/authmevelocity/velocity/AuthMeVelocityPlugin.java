package me.adrianed.authmevelocity.velocity;

import me.adrianed.authmevelocity.velocity.commands.AuthmeCommand;
import me.adrianed.authmevelocity.velocity.listener.ConnectListener;
import me.adrianed.authmevelocity.velocity.listener.FastLoginListener;
import me.adrianed.authmevelocity.velocity.listener.PluginMessageListener;
import me.adrianed.authmevelocity.velocity.listener.ProxyListener;
import me.adrianed.authmevelocity.api.velocity.AuthMeVelocityAPI;
import me.adrianed.authmevelocity.common.LibsManager;
import me.adrianed.authmevelocity.common.configuration.ConfigurationContainer;
import me.adrianed.authmevelocity.common.configuration.Loader;
import me.adrianed.authmevelocity.common.configuration.ProxyConfiguration;
import com.google.inject.Inject;
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

import net.byteflux.libby.VelocityLibraryManager;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.slf4j.Logger;

import java.util.function.Predicate;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Plugin(
    id = "authmevelocity",
    name = "AuthMeVelocity",
    url = "https://github.com/4drian3d/AuthMeVelocity",
    description = "This plugin adds the support for AuthMeReloaded to Velocity",
    version = "3.0.0",
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
    private ConfigurationContainer<ProxyConfiguration> config;

    protected final Set<UUID> loggedPlayers = ConcurrentHashMap.newKeySet();

    @Inject
    public AuthMeVelocityPlugin(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.pluginDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        final LibsManager libraries
            = new LibsManager(
                new VelocityLibraryManager<>(
                    logger, pluginDirectory, proxy.getPluginManager(), this));
        libraries.loadLibraries();

        this.config = Loader.loadMainConfig(pluginDirectory, ProxyConfiguration.class, logger);

        proxy.getChannelRegistrar().register(AUTHMEVELOCITY_CHANNEL);

        List.of(
            new ProxyListener(this),
            new ConnectListener(this, proxy, logger),
            new PluginMessageListener(proxy, logger, this)
        ).forEach(listener ->
            proxy.getEventManager().register(this, listener));

        if (proxy.getPluginManager().isLoaded("fastlogin")) {
            proxy.getEventManager().register(this, new FastLoginListener(proxy, this));
        }

        if (proxy.getPluginManager().isLoaded("miniplaceholders")) {
            AuthMePlaceholders.getExpansion(this).register();
        }

        AuthmeCommand.register(this, proxy.getCommandManager());

        this.sendInfoMessage();
    }

    protected ProxyServer getProxy(){
        return this.proxy;
    }

    public void sendInfoMessage() {
        CommandSource source = proxy.getConsoleCommandSource();
        source.sendMessage(MiniMessage.miniMessage().deserialize(
            " <gray>--- <aqua>AuthMeVelocity</aqua> ---"));
        source.sendMessage(MiniMessage.miniMessage().deserialize(
            "<gray>AuthServers: <green>" + config.get().authServers()));
        if (config.get().sendOnLogin().sendToServerOnLogin()) {
            source.sendMessage(MiniMessage.miniMessage().deserialize(
                "<gray>LobbyServers: <green>" + config.get().sendOnLogin().teleportServers()));
        }
    }

    public ConfigurationContainer<ProxyConfiguration> config() {
        return this.config;
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
        return config.get().authServers().contains(server.getServerInfo().getName());
    }

    @Override
    public boolean isAuthServer(ServerConnection connection){
        return config.get().authServers().contains(connection.getServerInfo().getName());
    }

    @Override
    public boolean isAuthServer(String server){
        return config.get().authServers().contains(server);
    }
}
