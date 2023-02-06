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

package me.adrianed.authmevelocity.paper.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.adrianed.authmevelocity.common.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import me.adrianed.authmevelocity.api.paper.event.LoginByProxyEvent;
import me.adrianed.authmevelocity.paper.AuthMeVelocityPlugin;

import fr.xephi.authme.api.v3.AuthMeApi;

public class MessageListener implements PluginMessageListener {
    private final AuthMeVelocityPlugin plugin;

    public MessageListener(AuthMeVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String identifier, @NotNull Player player, byte @NotNull [] bytes) {
        if (!identifier.equals("authmevelocity")) {
            plugin.logDebug("PluginMessage | Not AuthMeVelocity identifier");
            return;
        }

        plugin.logDebug("PluginMessage | AuthMeVelocity identifier");

        @SuppressWarnings("UnstableApiUsage")
        final ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        final String subChannel = input.readUTF();

        if ("main".equals(subChannel)) {
            plugin.logDebug("PluginMessage | Main Subchannel");
            final String msg = input.readUTF();
            if (MessageType.LOGIN.toString().equals(msg)) {
                plugin.logDebug("PluginMessage | Login Message");
                Bukkit.getPluginManager().callEvent(new LoginByProxyEvent(player));
                AuthMeApi.getInstance().forceLogin(player);
            }
        }
    }
}
