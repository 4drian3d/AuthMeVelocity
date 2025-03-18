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
            String message = event.getMessage();
            if (plugin.isLogged(event.getPlayer())) {
                plugin.logDebug(() -> "PlayerChatEvent | Player " + event.getPlayer().getUsername() + " is already logged");
                continuation.resume();
                return;
            }

            plugin.logDebug(() -> "PlayerChatEvent | Player " + event.getPlayer().getUsername() + " is not logged");

            if (plugin.config().get().chat().allowedChatPrefixes().stream().anyMatch(message::startsWith)) {
                plugin.logDebug(() -> "PlayerChatEvent | Message \"" + message + "\" is allowed by prefix rule.");
                continuation.resume();
                return;
            }

            plugin.logDebug(() -> "PlayerChatEvent | Message \"" + message + "\" is blocked.");
            sendBlockedChatMessage(event.getPlayer());
            event.setResult(PlayerChatEvent.ChatResult.denied());
            continuation.resume();
        });
    }

    private void sendBlockedChatMessage(final Player player){
        final String blockedChatMessage = plugin.config().get().chat().blockedChatMessage();
        if (!blockedChatMessage.isBlank()){
            player.sendMessage(MiniMessage.miniMessage().deserialize(blockedChatMessage));
        }
    }
}
