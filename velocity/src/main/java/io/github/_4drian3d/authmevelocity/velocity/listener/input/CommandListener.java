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
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import io.github._4drian3d.authmevelocity.velocity.listener.Listener;
import io.github._4drian3d.authmevelocity.velocity.utils.AuthMeUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class CommandListener implements Listener<CommandExecuteEvent> {
    @Inject
    private EventManager eventManager;
    @Inject
    private AuthMeVelocityPlugin plugin;

    @Override
    public void register() {
        eventManager.register(plugin, CommandExecuteEvent.class, PostOrder.FIRST, this);
    }

    @Override
    public EventTask executeAsync(final CommandExecuteEvent event) {
        return EventTask.withContinuation(continuation -> {
            if (!(event.getCommandSource() instanceof final Player player)) {
                plugin.logDebug(() -> "CommandExecuteEvent | CommandSource is not a player");
                continuation.resume();
                return;
            }

            if (plugin.isLogged(player)) {
                plugin.logDebug(() -> "CommandExecuteEvent | Player "+ player.getUsername() +" is already logged");
                continuation.resume();
                return;
            }

            if (plugin.isInAuthServer(player)) {
                plugin.logDebug(() -> "CommandExecuteEvent | Player "+ player.getUsername() +" is in Auth Server");
                final String command = AuthMeUtils.getFirstArgument(event.getCommand());
                if (!plugin.config().get().commands().allowedCommands().contains(command)) {
                    plugin.logDebug(() -> "CommandExecuteEvent | Player "+ player.getUsername() +" executed an blocked command");
                    sendBlockedMessage(player);
                    event.setResult(CommandExecuteEvent.CommandResult.denied());
                }
            } else {
                plugin.logDebug(() -> "CommandExecuteEvent | Player "+ player.getUsername() +" is not in auth server");
                sendBlockedMessage(player);
                event.setResult(CommandExecuteEvent.CommandResult.denied());
            }
            continuation.resume();
        });
    }

    private void sendBlockedMessage(final Player player){
        final String blockedMessage = plugin.config().get().commands().blockedCommandMessage();
        if (!blockedMessage.isBlank()){
            player.sendMessage(MiniMessage.miniMessage().deserialize(blockedMessage));
        }
    }
}
