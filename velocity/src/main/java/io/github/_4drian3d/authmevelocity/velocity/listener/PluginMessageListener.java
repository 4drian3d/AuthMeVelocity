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

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.authmevelocity.api.velocity.event.*;
import io.github._4drian3d.authmevelocity.common.MessageType;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import io.github._4drian3d.authmevelocity.velocity.utils.AuthMeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Locale;

public class PluginMessageListener {
    private final ProxyServer proxy;
    private final Logger logger;
    private final AuthMeVelocityPlugin plugin;

    public PluginMessageListener(@NotNull ProxyServer proxy, @NotNull Logger logger, AuthMeVelocityPlugin plugin) {
        this.proxy = proxy;
        this.logger = logger;
        this.plugin = plugin;
    }

    @Subscribe
    public void onPluginMessage(final PluginMessageEvent event, Continuation continuation) {
        final boolean cancelled = !event.getResult().isAllowed()
            || !(event.getSource() instanceof ServerConnection)
            || !event.getIdentifier().equals(AuthMeVelocityPlugin.AUTHMEVELOCITY_CHANNEL);
        if (cancelled) {
            continuation.resume();
            plugin.logDebug("PluginMessageEvent | Not allowed");
            return;
        }

        final ServerConnection connection = (ServerConnection) event.getSource();

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        final ByteArrayDataInput input = event.dataAsDataStream();
        final String message = input.readUTF();
        final MessageType type = MessageType.valueOf(
            message.toUpperCase(Locale.ROOT));
        final String name = input.readUTF();
        final @Nullable Player player = proxy.getPlayer(name).orElse(null);

        switch (type) {
            case LOGIN -> {
                plugin.logDebug("PluginMessageEvent | Login type");
                if (player != null && plugin.addPlayer(player)) {
                    proxy.getEventManager().fireAndForget(new ProxyLoginEvent(player));
                    if (plugin.config().get().sendOnLogin().sendToServerOnLogin()) {
                        this.createServerConnectionRequest(player, connection);
                    }
                    plugin.logDebug("PluginMessageEvent | Player not null");
                }
            }
            case LOGOUT -> {
                plugin.logDebug("PluginMessageEvent | Logout type");
                if (player != null && plugin.removePlayer(player)){
                    proxy.getEventManager().fireAndForget(new ProxyLogoutEvent(player));
                    plugin.logDebug("PluginMessageEvent | Player not null");
                }
            }
            case REGISTER -> {
                plugin.logDebug("PluginMessageEvent | Register");
                if (player != null) {
                    proxy.getEventManager().fireAndForget(new ProxyRegisterEvent(player));
                    plugin.logDebug("PluginMessageEvent | Player not null");
                }
            }
            case UNREGISTER -> {
                plugin.logDebug("PluginMessageEvent | Unregister type");
                if (player != null) {
                    plugin.logDebug("PluginMessageEvent | Player not null");
                    proxy.getEventManager().fireAndForget(new ProxyUnregisterEvent(player));
                } 
            }
            case FORCE_UNREGISTER -> {
                proxy.getEventManager().fireAndForget(new ProxyForcedUnregisterEvent(player));
                plugin.logDebug("PluginMessageEvent | Forced Unregister type");
            }
                
        }
        continuation.resume();
    }

    private void createServerConnectionRequest(Player player, ServerConnection connection){
        final RegisteredServer loginServer = player.getCurrentServer().orElse(connection).getServer();

        final var config = plugin.config().get();

        final var toSend = AuthMeUtils.serverToSend(
            config.sendOnLogin().sendMode(), proxy, config.sendOnLogin().teleportServers(), config.advanced().randomAttempts());

        if (toSend.isEmpty()) {
            if (toSend.string() != null) {
                logger.warn("The server {} does not exist", toSend.string());
            } else {
                logger.warn("There is not valid server to send");
            }
            return;
        }

        proxy.getEventManager().fire(new PreSendOnLoginEvent(player, loginServer, toSend.object()))
                .thenAccept(event -> {
                    if (!event.getResult().isAllowed()) {
                        return;
                    }
                    player.createConnectionRequest(event.getResult().getServer())
                        .connect()
                        .thenAcceptAsync(result -> {
                            if (!result.isSuccessful()) {
                                logger.info("Unable to connect the player {} to the server {}",
                                    player.getUsername(),
                                    result.getAttemptedConnection().getServerInfo().getName());
                            }
                    });
                });
    }
}
