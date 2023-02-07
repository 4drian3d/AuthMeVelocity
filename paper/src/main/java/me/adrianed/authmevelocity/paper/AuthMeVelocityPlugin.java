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

package me.adrianed.authmevelocity.paper;

import me.adrianed.authmevelocity.paper.listeners.AuthMeListener;
import me.adrianed.authmevelocity.paper.listeners.MessageListener;
import me.adrianed.authmevelocity.common.configuration.ConfigurationContainer;
import me.adrianed.authmevelocity.common.configuration.Loader;
import me.adrianed.authmevelocity.common.configuration.PaperConfiguration;
import me.adrianed.authmevelocity.common.MessageType;
import me.adrianed.authmevelocity.common.LibsManager;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import net.byteflux.libby.BukkitLibraryManager;

import java.util.logging.Level;

public final class AuthMeVelocityPlugin extends JavaPlugin {
    private static final String CHANNEL = "authmevelocity:main";

    private ConfigurationContainer<PaperConfiguration> config;

    @Override
    public void onEnable() {
        new LibsManager(new BukkitLibraryManager(this)).loadLibraries();

        try {
            this.config = Loader.loadMainConfig(getDataFolder().toPath(), PaperConfiguration.class);
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
            final @NotNull String playername
    ) {
        @SuppressWarnings("UnstableApiUsage") final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(type.toString());
        out.writeUTF(playername);

        if (player == null) {
            logDebug("MessageToProxy | Null Player, Player Name: " + playername);
            Bukkit.getServer().sendPluginMessage(this, CHANNEL, out.toByteArray());
        } else {
            logDebug("MessageToProxy | Player Present: " + player.getName() + ", Player Name: " + playername);
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
