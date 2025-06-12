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

package io.github._4drian3d.authmevelocity.velocity.listener.input;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import io.github._4drian3d.authmevelocity.velocity.listener.Listener;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

public final class ChatListener implements Listener<PlayerChatEvent> {
    @Inject
    private AuthMeVelocityPlugin plugin;
    @Inject
    private EventManager eventManager;

    @Override
    public void register() {
        eventManager.register(plugin, PlayerChatEvent.class, PostOrder.FIRST, this);
    }

    @Override
    public EventTask executeAsync(final PlayerChatEvent event) {
        return EventTask.withContinuation(continuation -> {
            if (plugin.isLogged(event.getPlayer())) {
                plugin.logDebug(() -> "PlayerChatEvent | Player " + event.getPlayer().getUsername() + " is already logged");
                continuation.resume();
                return;
            }

            plugin.logDebug(() -> "PlayerChatEvent | Player " + event.getPlayer().getUsername() + " is not logged");

            final List<String> allowedServers = plugin.config().get().chat().serversThatDontRequireAuthForChat();
            if (!allowedServers.isEmpty() && player.getCurrentServer()
                    .map(server -> allowedServers.contains(server.getServerInfo().getName()))
                    .orElse(false)) {
                plugin.logDebug(() -> "PlayerChatEvent | Player " + event.getPlayer().getUsername() + " is on allowed server for chat");
                continuation.resume();
                return;
            }

            if (plugin.config().get().chat().enableAllowedChatPrefixes()
                    && !plugin.config().get().chat().allowedChatPrefixes().isEmpty()
                    && isMessageAllowed(event.getMessage(), plugin.config().get().chat().allowedChatPrefixes())) {
                plugin.logDebug(() -> "PlayerChatEvent | Allowed message: " + event.getMessage());
                continuation.resume();
                return;
            }

            plugin.logDebug(() -> "PlayerChatEvent | Blocked message: " + event.getMessage());
            event.setResult(PlayerChatEvent.ChatResult.denied());
            continuation.resume();
        });
    }

    private boolean isMessageAllowed(String message, List<String> allowedPrefixes) {
        for (String prefix : allowedPrefixes) {
            if (message.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
