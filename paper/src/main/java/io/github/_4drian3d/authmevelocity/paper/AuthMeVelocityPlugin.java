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

package io.github._4drian3d.authmevelocity.paper;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github._4drian3d.authmevelocity.common.LibsManager;
import io.github._4drian3d.authmevelocity.common.MessageType;
import io.github._4drian3d.authmevelocity.common.configuration.ConfigurationContainer;
import io.github._4drian3d.authmevelocity.common.configuration.PaperConfiguration;
import io.github._4drian3d.authmevelocity.paper.listeners.AuthMeListener;
import io.github._4drian3d.authmevelocity.paper.listeners.MessageListener;
import net.byteflux.libby.BukkitLibraryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public final class AuthMeVelocityPlugin extends JavaPlugin {
    private static final String CHANNEL = "authmevelocity:main";

    private ConfigurationContainer<PaperConfiguration> config;

    @Override
    public void onEnable() {
        new LibsManager(new BukkitLibraryManager(this)).loadLibraries();

        try {
            this.config = ConfigurationContainer.load(getDataFolder().toPath(), PaperConfiguration.class);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Could not load config.conf file", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        final var server = this.getServer();

        server.getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        server.getMessenger().registerIncomingPluginChannel(this, CHANNEL, new MessageListener(this));
        server.getPluginManager().registerEvents(new AuthMeListener(this), this);

        if (server.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            AuthMePlaceholders.getExpansion().register();
        }

        this.getLogger().info("AuthMeVelocity enabled");
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, CHANNEL);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, CHANNEL);

        this.getLogger().info("AuthMeVelocity disabled");
    }

    public void sendMessageToProxy(
            final Player player,
            final @NotNull MessageType type,
            final @NotNull String playerName
    ) {
        @SuppressWarnings("UnstableApiUsage") final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(type.toString());
        out.writeUTF(playerName);

        if (player == null) {
            logDebug("MessageToProxy | Null Player, Player Name: " + playerName);
            Bukkit.getServer().sendPluginMessage(this, CHANNEL, out.toByteArray());
        } else {
            logDebug("MessageToProxy | Player Present: " + player.getName() + ", Player Name: " + playerName);
            player.sendPluginMessage(this, CHANNEL, out.toByteArray());
        }
    }

    public void sendMessageToProxy(
            final Player player,
            final @NotNull MessageType type
    ) {
        sendMessageToProxy(player, type, player.getName());
    }

    public void logDebug(String debug) {
        if (config.get().debug()) {
            getLogger().info("[DEBUG] " + debug);
        }
    }
}
