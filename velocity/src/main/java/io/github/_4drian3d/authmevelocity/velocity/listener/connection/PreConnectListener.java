/*
 * Copyright (C) 2026 AuthMeVelocity Contributors
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
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import io.github._4drian3d.authmevelocity.velocity.listener.Listener;

public final class PreConnectListener implements Listener<ServerPreConnectEvent> {
    @Inject
    private AuthMeVelocityPlugin plugin;
    @Inject
    private EventManager eventManager;

    @Override
    public void register() {
        eventManager.register(plugin, ServerPreConnectEvent.class, this);
    }

    @Override
    public EventTask executeAsync(final ServerPreConnectEvent event) {
        return EventTask.withContinuation(continuation -> {
            if (plugin.isLogged(event.getPlayer())) {
                plugin.logDebug(() -> "ServerPreConnectEvent | Player " + event.getPlayer().getUsername() + " is already logged");
                continuation.resume();
                return;
            }

            final RegisteredServer server = event.getResult().getServer().orElse(null);
            if (server == null) {
                plugin.logDebug(() -> "ServerPreConnectEvent | " + event.getPlayer().getUsername() + " | Null Server");
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
        });
    }
}
