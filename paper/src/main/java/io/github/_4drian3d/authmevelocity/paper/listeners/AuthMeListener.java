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

package io.github._4drian3d.authmevelocity.paper.listeners;

import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.LogoutEvent;
import fr.xephi.authme.events.RegisterEvent;
import fr.xephi.authme.events.UnregisterByAdminEvent;
import fr.xephi.authme.events.UnregisterByPlayerEvent;
import io.github._4drian3d.authmevelocity.api.paper.event.PreSendLoginEvent;
import io.github._4drian3d.authmevelocity.common.MessageType;
import io.github._4drian3d.authmevelocity.paper.AuthMeVelocityPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class AuthMeListener implements Listener {
    private final AuthMeVelocityPlugin plugin;

    public AuthMeListener(AuthMeVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(final LoginEvent event) {
        final Player player = event.getPlayer();
        plugin.logDebug("LoginEvent | Start");

        // I hate this, but... Spigot compatibility ¯\_(ツ)_/¯
        final var preSendLoginEvent = new PreSendLoginEvent(player);
        Bukkit.getPluginManager().callEvent(preSendLoginEvent);

        if (!preSendLoginEvent.isCancelled()) {
            plugin.sendMessageToProxy(player, MessageType.LOGIN, player.getName());
            plugin.getLogger().info("LoginEvent | PreSendLoginEvent allowed");
        }
    }

    @EventHandler
    public void onRegister(final RegisterEvent event) {
        plugin.logDebug("RegisterEvent | Executed");
        plugin.sendMessageToProxy(event.getPlayer(), MessageType.REGISTER);
    }

    @EventHandler
    public void onLogout(final LogoutEvent event) {
        plugin.logDebug("LogoutEvent | Executed");
        plugin.sendMessageToProxy(event.getPlayer(), MessageType.LOGOUT);
    }

    @EventHandler
    public void onUnRegister(final UnregisterByPlayerEvent event) {
        plugin.logDebug("UnregisterByPlayerEvent | Executed");
        plugin.sendMessageToProxy(event.getPlayer(), MessageType.UNREGISTER);
    }

    @EventHandler
    public void onAdminUnRegister(final UnregisterByAdminEvent event) {
        plugin.logDebug("UnregisterByAdminEvent | Executed");
        plugin.sendMessageToProxy(event.getPlayer(), MessageType.FORCE_UNREGISTER, event.getPlayerName());
    }
}
