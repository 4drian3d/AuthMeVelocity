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

package io.github._4drian3d.authmevelocity.velocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.authmevelocity.api.velocity.AuthMeVelocityAPI;
import io.github._4drian3d.authmevelocity.common.Constants;
import io.github._4drian3d.authmevelocity.common.LibsManager;
import io.github._4drian3d.authmevelocity.common.configuration.ConfigurationContainer;
import io.github._4drian3d.authmevelocity.common.configuration.ProxyConfiguration;
import io.github._4drian3d.authmevelocity.velocity.commands.AuthMeCommand;
import io.github._4drian3d.authmevelocity.velocity.hooks.AuthMeContexts;
import io.github._4drian3d.authmevelocity.velocity.hooks.AuthMePlaceholders;
import io.github._4drian3d.authmevelocity.velocity.listener.Listener;
import io.github._4drian3d.authmevelocity.velocity.listener.compat.FastLoginListener;
import io.github._4drian3d.authmevelocity.velocity.listener.connection.DisconnectListener;
import io.github._4drian3d.authmevelocity.velocity.listener.connection.InitialServerListener;
import io.github._4drian3d.authmevelocity.velocity.listener.connection.PostConnectListener;
import io.github._4drian3d.authmevelocity.velocity.listener.connection.PreConnectListener;
import io.github._4drian3d.authmevelocity.velocity.listener.data.PluginMessageListener;
import io.github._4drian3d.authmevelocity.velocity.listener.input.ChatListener;
import io.github._4drian3d.authmevelocity.velocity.listener.input.CommandListener;
import io.github._4drian3d.authmevelocity.velocity.listener.input.CompletionPacketListener;
import io.github._4drian3d.authmevelocity.velocity.listener.input.TabCompleteListener;
import net.byteflux.libby.VelocityLibraryManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Plugin(
    id = "authmevelocity",
    name = "AuthMeVelocity",
    url = "https://modrinth.com/plugin/authmevelocity",
    description = "AuthMeReloaded Support for Velocity",
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
        ),
        @Dependency(
            id = "vpacketevents",
            optional = true
        ),
        @Dependency(
            id = "luckperms",
            optional = true
        )
    }
)
public final class AuthMeVelocityPlugin implements AuthMeVelocityAPI {
    public static final ChannelIdentifier MODERN_CHANNEL
        = MinecraftChannelIdentifier.create("authmevelocity", "main");
    public static final ChannelIdentifier LEGACY_CHANNEL
            = new LegacyChannelIdentifier("authmevelocity:main");
    @Inject
    private ProxyServer proxy;
    @Inject
    private PluginManager pluginManager;
    @Inject
    private Logger logger;
    @Inject
    @DataDirectory
    private Path pluginDirectory;
    @Inject
    private Metrics.Factory metricsFactory;
    @Inject
    private Injector injector;
    private ConfigurationContainer<ProxyConfiguration> config;

    final Set<UUID> loggedPlayers = ConcurrentHashMap.newKeySet();

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        final LibsManager libraries
            = new LibsManager(
                new VelocityLibraryManager<>(
                    logger, pluginDirectory, pluginManager, this));
        libraries.loadLibraries();

        try {
            this.config = ConfigurationContainer.load(pluginDirectory, ProxyConfiguration.class);
        } catch (Exception e) {
            logger.error("Could not load config.conf file", e);
            return;
        }

        logDebug("Loaded plugin libraries");

        final int pluginId = 16128;
        final Metrics metrics = metricsFactory.make(this, pluginId);

        proxy.getChannelRegistrar().register(MODERN_CHANNEL, LEGACY_CHANNEL);

        Stream.of(
                PluginMessageListener.class,
                DisconnectListener.class,
                InitialServerListener.class,
                PostConnectListener.class,
                PreConnectListener.class,
                ChatListener.class,
                CommandListener.class,
                TabCompleteListener.class
        ).map(injector::getInstance)
        .forEach(Listener::register);

        final boolean fastlogin = pluginManager.isLoaded("fastlogin");
        metrics.addCustomChart(new SimplePie("fastlogin_compatibility", () -> Boolean.toString(fastlogin)));
        if (fastlogin) {
            logDebug("Register FastLogin compatibility");
            injector.getInstance(FastLoginListener.class).register();
        }

        final boolean miniplaceholders = pluginManager.isLoaded("miniplaceholders");
        metrics.addCustomChart(new SimplePie("miniplaceholders_compatibility", () -> Boolean.toString(miniplaceholders)));
        if (miniplaceholders) {
            logDebug("Register MiniPlaceholders compatibility");
            injector.getInstance(AuthMePlaceholders.class).getExpansion().register();
        }

        final boolean vpacketevents = pluginManager.isLoaded("vpacketevents");
        metrics.addCustomChart(new SimplePie("vpacketevents_listener", () -> Boolean.toString(vpacketevents)));
        if (vpacketevents) {
            injector.getInstance(CompletionPacketListener.class).register();
        }

        if (pluginManager.isLoaded("luckperms")) {
            this.injector.getInstance(AuthMeContexts.class).register();
        }

        injector.getInstance(AuthMeCommand.class).register();

        this.sendInfoMessage();
    }

    public void sendInfoMessage() {
        final CommandSource source = proxy.getConsoleCommandSource();
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
        loggedPlayers.removeIf(uuid -> proxy.getPlayer(uuid).filter(predicate).isPresent());
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

    public void logDebug(final String msg) {
        if (config.get().advanced().debug()) {
            logger.info("[DEBUG] {}", msg);
        }
    }

    public void logDebug(final Supplier<String> msg) {
        if (config.get().advanced().debug()) {
            logger.info("[DEBUG] {}", msg.get());
        }
    }
}
