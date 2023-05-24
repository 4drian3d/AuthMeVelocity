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

package io.github._4drian3d.authmevelocity.velocity.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.authmevelocity.common.configuration.ProxyConfiguration;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import io.github._4drian3d.authmevelocity.velocity.utils.AuthMeUtils;
import io.github._4drian3d.authmevelocity.velocity.utils.Pair;
import org.slf4j.Logger;

import java.util.Optional;

public final class ConnectListener {
    @Inject
    private ProxyServer proxy;
    @Inject
    private Logger logger;
    @Inject
    private AuthMeVelocityPlugin plugin;

    @Subscribe(order = PostOrder.LATE)
    public void onInitialServer(
            final PlayerChooseInitialServerEvent event,
            Continuation continuation
    ) {
        if (!plugin.config().get().ensureAuthServer().ensureFirstServerIsAuthServer()) {
            continuation.resume();
            plugin.logDebug("PlayerChooseInitialServerEvent | Not enabled");
            return;
        }

        final Optional<RegisteredServer> optionalSV = event.getInitialServer();
        if (optionalSV.isPresent() && plugin.isAuthServer(optionalSV.get())) {
            continuation.resume();
            plugin.logDebug("PlayerChooseInitialServerEvent | Player is in auth server");
            return;
        }
        final ProxyConfiguration config = plugin.config().get();
        final Pair<RegisteredServer> server = AuthMeUtils.serverToSend(
                config.ensureAuthServer().sendMode(), proxy, config.authServers(), config.advanced().randomAttempts());

        // Velocity takes over in case the initial server is not present
        event.setInitialServer(server.object());
        continuation.resume();
        if (server.isEmpty()) {
            plugin.logDebug("PlayerChooseInitialServerEvent | Null server");
            logger.error("Cannot send the player {} to an auth server", event.getPlayer().getUsername());
        }
    }

    @Subscribe
    public void onServerPreConnect(
            final ServerPreConnectEvent event,
            final Continuation continuation
    ) {
        if (plugin.isLogged(event.getPlayer())) {
            plugin.logDebug("ServerPreConnectEvent | Player already logged");
            continuation.resume();
            return;
        }

        final RegisteredServer server = event.getResult().getServer().orElse(null);
        if (server == null) {
            plugin.logDebug("ServerPreConnectEvent | Null Server");
            continuation.resume();
            return;
        }
        // this should be present, "event.getResult().isAllowed()" is the "isPresent" check
        if (!plugin.isAuthServer(server)) {
            plugin.logDebug("ServerPreConnectEvent | Server "+server.getServerInfo().getName()+" is not an auth server");
            event.setResult(ServerPreConnectEvent.ServerResult.denied());
        } else {
            plugin.logDebug("ServerPreConnectEvent | Server "+server.getServerInfo().getName()+" is an auth server");
        }
        continuation.resume();
    }

    @SuppressWarnings("UnstableApiUsage")
    @Subscribe
    public void onServerPostConnect(final ServerPostConnectEvent event) {
        final Player player = event.getPlayer();

        final boolean isLogged = plugin.isLogged(player);
        plugin.logDebug("ServerPostConnectEvent | Player "+player.getUsername()+" is logged: " + isLogged);
        final RegisteredServer server = player.getCurrentServer().map(ServerConnection::getServer).orElse(null);
        if (server == null) {
            plugin.logDebug("ServerPostConnectEvent | Player "+player.getUsername()+" is not in a server");
            return;
        }
        final boolean isInAuthServer = plugin.isInAuthServer(player);
        plugin.logDebug("ServerPostConnectEvent | Player "+player.getUsername()+" is in AuthServer: " + isInAuthServer);

        if (!(isLogged && isInAuthServer)) {
            return;
        }

        plugin.logDebug("ServerPostConnectEvent | Already logged player and connected to an Auth server");
        final ByteArrayDataOutput buf = ByteStreams.newDataOutput();
        buf.writeUTF("LOGIN");

        final byte[] byteArray = buf.toByteArray();
        plugin.logDebug("ServerPostConnectEvent | Sending LOGIN data");
        if (server.sendPluginMessage(AuthMeVelocityPlugin.MODERN_CHANNEL, byteArray)) {
            plugin.logDebug("ServerPostConnectEvent | Correctly send data");
        } else {
            plugin.logDebug("ServerPostConnectEvent | Failed to send data");
        }
    }
}
