/*
 * Copyright (C) 2025 AuthMeVelocity Contributors
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

package io.github._4drian3d.authmevelocity.velocity.listener.connection;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import io.github._4drian3d.authmevelocity.velocity.listener.Listener;

@SuppressWarnings("UnstableApiUsage")
public final class PostConnectListener implements Listener<ServerPostConnectEvent> {
    @Inject
    private AuthMeVelocityPlugin plugin;
    @Inject
    private EventManager eventManager;

    @Override
    public void register() {
        eventManager.register(plugin, ServerPostConnectEvent.class, this);
    }

    @Override
    public EventTask executeAsync(final ServerPostConnectEvent event) {
        return EventTask.async(() -> {
            final Player player = event.getPlayer();

            final boolean isLogged = plugin.isLogged(player);
            plugin.logDebug(() -> "ServerPostConnectEvent | Player "+player.getUsername()+" is logged: " + isLogged);
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
            final boolean messageResult = server.sendPluginMessage(AuthMeVelocityPlugin.MODERN_CHANNEL, (encoder) -> {
                plugin.logDebug(() -> "ServerPostConnectEvent | " + player.getUsername() + " | Encoding LOGIN data");
                encoder.writeUTF("LOGIN");
                encoder.writeUTF(player.getUsername());
                plugin.logDebug(() -> "ServerPostConnectEvent | " + player.getUsername() + " | Sending LOGIN data");
            });
            if (messageResult) {
                plugin.logDebug(() -> "ServerPostConnectEvent | " + player.getUsername() + " | Correctly send data");
            } else {
                plugin.logDebug("ServerPostConnectEvent | Failed to send data");
            }
        });
    }
}
