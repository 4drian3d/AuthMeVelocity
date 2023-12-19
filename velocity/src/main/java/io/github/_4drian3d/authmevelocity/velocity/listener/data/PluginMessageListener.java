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

package io.github._4drian3d.authmevelocity.velocity.listener.data;

import com.google.common.io.ByteArrayDataInput;
import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.authmevelocity.api.velocity.event.*;
import io.github._4drian3d.authmevelocity.common.MessageType;
import io.github._4drian3d.authmevelocity.common.configuration.ProxyConfiguration;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import io.github._4drian3d.authmevelocity.velocity.listener.Listener;
import io.github._4drian3d.authmevelocity.velocity.utils.AuthMeUtils;
import io.github._4drian3d.authmevelocity.velocity.utils.Pair;
import net.kyori.adventure.util.Index;
import org.slf4j.Logger;

import java.util.Locale;

public final class PluginMessageListener implements Listener<PluginMessageEvent> {
    private static final Index<String, MessageType> TYPES = Index.create(MessageType.class, Enum::toString);
    @Inject
    private ProxyServer proxy;
    @Inject
    private EventManager eventManager;
    @Inject
    private Logger logger;
    @Inject
    private AuthMeVelocityPlugin plugin;

    @Override
    public void register() {
        eventManager.register(plugin, PluginMessageEvent.class, this);
    }

    @Override
    public EventTask executeAsync(final PluginMessageEvent event) {
        return EventTask.async(() -> {
            plugin.logDebug(() -> "PluginMessageEvent | Start");
            if (notAllowedEvent(event)) {
                plugin.logDebug(() -> "PluginMessageEvent | Not allowed");
                return;
            }

            final ServerConnection connection = (ServerConnection) event.getSource();

            event.setResult(PluginMessageEvent.ForwardResult.handled());

            final ByteArrayDataInput input = event.dataAsDataStream();
            final String message = input.readUTF();
            final MessageType type = TYPES.valueOrThrow(message.toUpperCase(Locale.ROOT));
            final String name = input.readUTF();
            final Player player = proxy.getPlayer(name).orElse(null);

            switch (type) {
                case LOGIN -> {
                    plugin.logDebug("PluginMessageEvent | Login type");
                    if (player != null && plugin.addPlayer(player)) {
                        eventManager.fireAndForget(new ProxyLoginEvent(player));
                        if (plugin.config().get().sendOnLogin().sendToServerOnLogin()) {
                            this.createServerConnectionRequest(player, connection);
                        }
                        plugin.logDebug("PluginMessageEvent | Player not null");
                    }
                }
                case LOGOUT -> {
                    plugin.logDebug("PluginMessageEvent | Logout type");
                    if (player != null && plugin.removePlayer(player)){
                        eventManager.fireAndForget(new ProxyLogoutEvent(player));
                        plugin.logDebug(() -> "PluginMessageEvent | Player " + name + " not null");
                    }
                }
                case REGISTER -> {
                    plugin.logDebug("PluginMessageEvent | Register");
                    if (player != null) {
                        eventManager.fireAndForget(new ProxyRegisterEvent(player));
                        plugin.logDebug(() -> "PluginMessageEvent | Player " + name + " not null");
                    }
                }
                case UNREGISTER -> {
                    plugin.logDebug("PluginMessageEvent | Unregister type");
                    if (player != null) {
                        plugin.logDebug(() -> "PluginMessageEvent | Player " + name + " not null");
                        eventManager.fireAndForget(new ProxyUnregisterEvent(player));
                    }
                }
                case FORCE_UNREGISTER -> {
                    eventManager.fireAndForget(new ProxyForcedUnregisterEvent(player));
                    plugin.logDebug(() -> "PluginMessageEvent | Forced Unregister type, player " + name);
                }

            }
        });
    }

    private boolean notAllowedEvent(PluginMessageEvent event) {
        if (!event.getResult().isAllowed()) {
            plugin.logDebug("PluginMessageEvent | Result not allowed");
            return true;
        }
        if (!(event.getSource() instanceof ServerConnection)) {
            plugin.logDebug("PluginMessageEvent | Not ServerConnection");
            return true;
        }
        final var identifier = event.getIdentifier();
        if (!(identifier.equals(AuthMeVelocityPlugin.MODERN_CHANNEL)
                || identifier.equals(AuthMeVelocityPlugin.LEGACY_CHANNEL))) {
            plugin.logDebug(() -> "PluginMessageEvent | Not AuthMeVelocity Identifier: " + identifier.getId());
            return true;
        }
        return false;
    }

    private void createServerConnectionRequest(final Player player, final ServerConnection connection){
        final RegisteredServer loginServer = player.getCurrentServer().orElse(connection).getServer();

        final ProxyConfiguration config = plugin.config().get();

        final Pair<RegisteredServer> toSend = AuthMeUtils.serverToSend(
                config.sendOnLogin().sendMode(), proxy, config.sendOnLogin().teleportServers(), config.advanced().randomAttempts());

        if (toSend.isEmpty()) {
            if (toSend.string() != null) {
                logger.warn("The server {} does not exist", toSend.string());
            } else {
                logger.warn("There is not valid server to send");
            }
            return;
        }

        if (plugin.config().get().sendOnLogin().isRequirePermission() && !player.hasPermission("authmevelocity.send-on-login")) {
            plugin.logDebug(() -> "PluginMessageEvent # createServerConnectionRequest | The player does not have permission " + player.getUsername());
            return;
        }

        eventManager.fire(new PreSendOnLoginEvent(player, loginServer, toSend.object()))
                .thenAccept(event -> {
                    if (!event.getResult().isAllowed()) {
                        return;
                    }
                    player.createConnectionRequest(event.getResult().getServer())
                            .connect()
                            .thenAccept(result -> {
                                if (!result.isSuccessful()) {
                                    logger.info("Unable to connect the player {} to the server {}",
                                            player.getUsername(),
                                            result.getAttemptedConnection().getServerInfo().getName());
                                }
                            });
                });
    }
}
