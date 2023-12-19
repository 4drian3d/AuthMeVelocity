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
import net.byteflux.libby.LibraryManager;
import net.byteflux.libby.PaperLibraryManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public final class AuthMeVelocityPlugin extends JavaPlugin {
    private static final String CHANNEL = "authmevelocity:main";
    private final Path dataFolder;
    private ComponentLogger componentLogger;
    private ConfigurationContainer<PaperConfiguration> config;

    public AuthMeVelocityPlugin(final Path dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public void onEnable() {
        this.componentLogger = getComponentLogger();
        final LibraryManager libraryManager = new PaperLibraryManager(this);
        new LibsManager(libraryManager).loadLibraries();

        try {
            this.config = ConfigurationContainer.load(dataFolder, PaperConfiguration.class);
        } catch (Throwable e) {
            componentLogger.error("Could not load config.conf file", e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        final var server = this.getServer();

        server.getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        server.getMessenger().registerIncomingPluginChannel(this, CHANNEL, new MessageListener(this));
        server.getPluginManager().registerEvents(new AuthMeListener(this), this);

        if (server.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            AuthMePlaceholders.getExpansion().register();
        }

        componentLogger.info(miniMessage().deserialize("<gradient:aqua:dark_aqua>AuthMeVelocity</gradient> <aqua>enabled"));
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, CHANNEL);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, CHANNEL);

        componentLogger.info(miniMessage().deserialize("<gradient:aqua:dark_aqua>AuthMeVelocity</gradient> <red>disabled"));
    }

    public void sendMessageToProxy(
            final Player player,
            final @NotNull MessageType type,
            final @NotNull String playerName
    ) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(type.toString());
        out.writeUTF(playerName);

        if (player == null) {
            Bukkit.getServer().sendPluginMessage(this, CHANNEL, out.toByteArray());
            logDebug("MessageToProxy | Null Player, Player Name: " + playerName);
        } else {
            player.sendPluginMessage(this, CHANNEL, out.toByteArray());
            logDebug("MessageToProxy | Player Present: " + player.getName() + ", Player Name: " + playerName);
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
            componentLogger.info("[DEBUG] " + debug);
        }
    }
}
