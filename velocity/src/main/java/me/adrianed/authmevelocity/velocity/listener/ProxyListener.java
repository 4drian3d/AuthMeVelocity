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

package me.adrianed.authmevelocity.velocity.listener;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import me.adrianed.authmevelocity.velocity.AuthMeVelocityPlugin;
import me.adrianed.authmevelocity.velocity.utils.AuthmeUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class ProxyListener {
    private final AuthMeVelocityPlugin plugin;

    public ProxyListener(AuthMeVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public EventTask onDisconnect(final DisconnectEvent event) {
        if (event.getLoginStatus() == DisconnectEvent.LoginStatus.CONFLICTING_LOGIN) {
            return null;
        }

        return EventTask.async(() -> plugin.removePlayer(event.getPlayer()));
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onCommandExecute(final CommandExecuteEvent event, Continuation continuation) {
        if (!(event.getCommandSource() instanceof final Player player)){
            plugin.logDebug("CommandExecuteEvent | CommandSource is not a player");
            continuation.resume();
            return;
        }

        if (plugin.isLogged(player)) {
            plugin.logDebug("CommandExecuteEvent | Player is already logged");
            continuation.resume();
            return;
        }

        if (canBeIgnored(player)) {
            plugin.logDebug("CommandExecuteEvent | Ignored signed player");
            continuation.resume();
            return;
        }

        if (plugin.isInAuthServer(player)) {
            plugin.logDebug("CommandExecuteEvent | Player is in Auth Server");
            String command = AuthmeUtils.getFirstArgument(event.getCommand());
            if (!plugin.config().get().commands().allowedCommands().contains(command)) {
                plugin.logDebug("CommandExecuteEvent | Player executed an blocked command");
                sendBlockedMessage(player);
                event.setResult(CommandExecuteEvent.CommandResult.denied());
            }
        } else {
            plugin.logDebug("CommandExecuteEven | Player is not in auth server");
            sendBlockedMessage(player);
            event.setResult(CommandExecuteEvent.CommandResult.denied());
        }
        continuation.resume();
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerChat(final PlayerChatEvent event, Continuation continuation) {
        if (plugin.isLogged(event.getPlayer())) {
            plugin.logDebug("PlayerChatEvent | Player is already logged");
            continuation.resume();
            return;
        }

        plugin.logDebug("PlayerChatEvent | Player is not logged");

        if (canBeIgnored(event.getPlayer())) {
            plugin.logDebug("PlayerChatEvent | Ignored signed player");
            continuation.resume();
            return;
        }

        event.setResult(PlayerChatEvent.ChatResult.denied());
        continuation.resume();
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onTabComplete(TabCompleteEvent event){
        if (plugin.isLogged(event.getPlayer())) {
            plugin.logDebug("TabCompleteEvent | Player is already logged");
            return;
        }

        final String command = event.getPartialMessage();
        for (final String allowed : plugin.config().get().commands().allowedCommands()) {
            if (allowed.startsWith(command)) {
                return;
            }
        }

        plugin.logDebug("TabCompleteEvent | Not allowed tab-completion");
        event.getSuggestions().clear();
    }

    void sendBlockedMessage(Player player){
        String blockedMessage = plugin.config().get().commands().blockedCommandMessage();
        if (!blockedMessage.isBlank()){
            player.sendMessage(MiniMessage.miniMessage().deserialize(blockedMessage));
        }
    }

    boolean canBeIgnored(Player player) {
        return player.getIdentifiedKey() != null
            && player.getProtocolVersion().compareTo(ProtocolVersion.MINECRAFT_1_19_1) >= 0
            && plugin.config().get().advanced().ignoreSignedPlayers();
    }

}
