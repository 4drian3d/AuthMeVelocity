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

package io.github._4drian3d.authmevelocity.api.velocity;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * API provided to interact with logged players
 */
public interface AuthMeVelocityAPI {

    /**
     * Check if the player is logged in or not
     * @param player the player
     * @return if the player is logged in or not
     */
    boolean isLogged(@NotNull Player player);

    /**
     * Check if the player is not logged
     * @param player the player
     * @return if the player is not logged
     */
    boolean isNotLogged(@NotNull Player player);

    /**
     * Adds a player to the list of logged in players
     * @param player the new logged player
     * @return if the player was successfully added
     */
    boolean addPlayer(@NotNull Player player);

    /**
     * Removes a player from the list of logged-in players
     * @param player the unlogged player
     * @return if the player was successfully removed
     */
    boolean removePlayer(@NotNull Player player);

    /**
     * Removes players who meet the established condition
     * @param predicate the condition
     */
    void removePlayerIf(@NotNull Predicate<@NotNull Player> predicate);

    /**
     * Check if the player is on a login server
     * @param player the player
     * @return if the player is on a login server
     */
    boolean isInAuthServer(@NotNull Player player);

    /**
     * Check if a server is intended to be a logging server
     * @param server the server
     * @return if the server is a login server
     */
    boolean isAuthServer(@NotNull RegisteredServer server);

    /**
     * Checks if a connection is made from a login server
     * @param connection the connection
     * @return if the connection is made from a login server
     */
    boolean isAuthServer(@NotNull ServerConnection connection);

    /**
     * Checks if a string is a name of an auth server
     * @param server the server name
     * @return if the server is an auth server
     */
    boolean isAuthServer(@NotNull String server);

    /**
     * Adds a server to the list of auth servers
     * @param server the server name
     */
    void addAuthServer(@NotNull String server);

    /**
     * Removes a server from the list of auth servers
     * @param server the server name
     */
    void removeAuthServer(@NotNull String server);

    /**
     * Removes servers that meet the established condition
     * @param predicate the condition
     */
    void removeAuthServerIf(@NotNull Predicate<String> predicate);
}
