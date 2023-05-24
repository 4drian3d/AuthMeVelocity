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

package io.github._4drian3d.authmevelocity.velocity.listener.connection;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.authmevelocity.common.configuration.ProxyConfiguration;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import io.github._4drian3d.authmevelocity.velocity.listener.Listener;
import io.github._4drian3d.authmevelocity.velocity.utils.AuthMeUtils;
import io.github._4drian3d.authmevelocity.velocity.utils.Pair;
import org.slf4j.Logger;

import java.util.Optional;

public final class InitialServerListener implements Listener<PlayerChooseInitialServerEvent> {
    @Inject
    private AuthMeVelocityPlugin plugin;
    @Inject
    private EventManager eventManager;
    @Inject
    private ProxyServer proxy;
    @Inject
    private Logger logger;

    @Override
    public void register() {
        eventManager.register(plugin, PlayerChooseInitialServerEvent.class, PostOrder.LATE, this);
    }

    @Override
    public EventTask executeAsync(final PlayerChooseInitialServerEvent event) {
        return EventTask.withContinuation(continuation -> {
            final ProxyConfiguration config = plugin.config().get();
            if (!config.ensureAuthServer().ensureFirstServerIsAuthServer()) {
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

            final Pair<RegisteredServer> server = AuthMeUtils.serverToSend(
                    config.ensureAuthServer().sendMode(), proxy, config.authServers(), config.advanced().randomAttempts());

            // Velocity takes over in case the initial server is not present
            event.setInitialServer(server.object());
            continuation.resume();
            if (server.isEmpty()) {
                plugin.logDebug("PlayerChooseInitialServerEvent | Null server");
                logger.error("Cannot send the player {} to an auth server", event.getPlayer().getUsername());
            }
        });
    }
}
