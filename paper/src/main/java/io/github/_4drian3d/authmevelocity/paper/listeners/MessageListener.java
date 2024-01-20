/*
 * Copyright (C) 2024 AuthMeVelocity Contributors
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

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.xephi.authme.api.v3.AuthMeApi;
import io.github._4drian3d.authmevelocity.api.paper.event.LoginByProxyEvent;
import io.github._4drian3d.authmevelocity.common.MessageType;
import io.github._4drian3d.authmevelocity.paper.AuthMeVelocityPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public final class MessageListener implements PluginMessageListener {
    private final AuthMeVelocityPlugin plugin;

    public MessageListener(final AuthMeVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(
            final @NotNull String identifier,
            final @NotNull Player $,
            final byte @NotNull [] bytes
    ) {
        if (identifier.equals("authmevelocity:main")) {
            final ByteArrayDataInput input = ByteStreams.newDataInput(bytes);

            final String data = input.readUTF();
            final String username = input.readUTF();
            processData(username, data);
            plugin.logDebug("PluginMessage | AuthMeVelocity identifier processed");
        }
    }

    private void processData(String name, String data) {
        if (MessageType.LOGIN.toString().equals(data)) {
            final Player player = this.plugin.getServer().getPlayer(name);
            if (player == null) {
                return;
            }
            plugin.logDebug("PluginMessage | Login Message");
            Bukkit.getPluginManager().callEvent(new LoginByProxyEvent(player));
            AuthMeApi.getInstance().forceLogin(player);
        }
    }
}
