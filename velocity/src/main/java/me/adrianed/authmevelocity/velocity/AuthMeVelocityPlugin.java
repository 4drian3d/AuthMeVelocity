/*
 * Copyright (C) 2023 AuthMeVelocity Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.adrianed.authmevelocity.velocity;

import me.adrianed.authmevelocity.velocity.commands.AuthmeCommand;
import me.adrianed.authmevelocity.velocity.listener.ConnectListener;
import me.adrianed.authmevelocity.velocity.listener.FastLoginListener;
import me.adrianed.authmevelocity.velocity.listener.PluginMessageListener;
import me.adrianed.authmevelocity.velocity.listener.ProxyListener;
import me.adrianed.authmevelocity.api.velocity.AuthMeVelocityAPI;
import me.adrianed.authmevelocity.common.Constants;
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

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;

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
    description = Constants.DESCRIPTION,
    version = Constants.VERSION,
    authors = {"xQuickGlare", "4drian3d"},
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
    private final Metrics.Factory metricsFactory;
    private ConfigurationContainer<ProxyConfiguration> config;

    final Set<UUID> loggedPlayers = ConcurrentHashMap.newKeySet();

    @Inject
    public AuthMeVelocityPlugin(
        ProxyServer proxy,
        Logger logger,
        @DataDirectory Path dataDirectory,
        Metrics.Factory factory
    ) {
        this.proxy = proxy;
        this.logger = logger;
        this.pluginDirectory = dataDirectory;
        this.metricsFactory = factory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        final LibsManager libraries
            = new LibsManager(
                new VelocityLibraryManager<>(
                    logger, pluginDirectory, proxy.getPluginManager(), this));
        libraries.loadLibraries();

        try {
            this.config = Loader.loadMainConfig(pluginDirectory, ProxyConfiguration.class);
        } catch (Exception e) {
            logger.error("Could not load config.conf file", e);
            return;
        }

        logDebug("Loaded plugin libraries");

        final int pluginId = 16128;
        Metrics metrics = metricsFactory.make(this, pluginId);

        proxy.getChannelRegistrar().register(AUTHMEVELOCITY_CHANNEL);

        List.of(
            new ProxyListener(this),
            new ConnectListener(this, proxy, logger),
            new PluginMessageListener(proxy, logger, this)
        ).forEach(listener ->
            proxy.getEventManager().register(this, listener));

        final boolean fastlogin = proxy.getPluginManager().isLoaded("fastlogin");
        metrics.addCustomChart(new SimplePie("fastlogin_compatibility", () -> Boolean.toString(fastlogin)));
        if (fastlogin) {
            logDebug("Register FastLogin compatibility");
            proxy.getEventManager().register(this, new FastLoginListener(proxy, this));
        }

        final boolean miniplaceholders = proxy.getPluginManager().isLoaded("miniplaceholders");
        metrics.addCustomChart(new SimplePie("miniplaceholders_compatibility", () -> Boolean.toString(miniplaceholders)));
        if (miniplaceholders) {
            logDebug("Register MiniPlaceholders compatibility");
            AuthMePlaceholders.getExpansion(this).register();
        }

        AuthmeCommand.register(this, proxy.getCommandManager(), logger);

        this.sendInfoMessage();
    }

    ProxyServer getProxy(){
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
    public boolean isLogged(@NotNull Player player){
        return loggedPlayers.contains(player.getUniqueId());
    }

    @Override
    public boolean isNotLogged(@NotNull Player player){
        return !loggedPlayers.contains(player.getUniqueId());
    }

    @Override
    public boolean addPlayer(@NotNull Player player){
        return loggedPlayers.add(player.getUniqueId());
    }

    @Override
    public boolean removePlayer(@NotNull Player player){
        return loggedPlayers.remove(player.getUniqueId());
    }

    @Override
    public void removePlayerIf(@NotNull Predicate<Player> predicate){
        loggedPlayers.removeIf(uuid -> predicate.test(getProxy().getPlayer(uuid).orElse(null)));
    }

    @Override
    public boolean isInAuthServer(@NotNull Player player){
        return player.getCurrentServer().map(this::isAuthServer).orElse(false);
    }

    @Override
    public boolean isAuthServer(@NotNull RegisteredServer server){
        return isAuthServer(server.getServerInfo().getName());
    }

    @Override
    public boolean isAuthServer(@NotNull ServerConnection connection){
        return isAuthServer(connection.getServerInfo().getName());
    }

    @Override
    public boolean isAuthServer(@NotNull String server){
        return config.get().authServers().contains(server);
    }

    public void logDebug(String msg) {
        if (config.get().advanced().debug()) {
            logger.info("[DEBUG] {}", msg);
        }
    }
}
