package me.adrianed.authmevelocity.api.velocity;

import java.util.function.Predicate;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import org.jetbrains.annotations.NotNull;

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
     * @return if the player was succesfully added
     */
    boolean addPlayer(@NotNull Player player);

    /**
     * Removes a player from the list of logged-in players
     * @param player the unlogged player
     * @return if the player was succesfully removed
     */
    boolean removePlayer(@NotNull Player player);

    /**
     * Removes players who meet the established condition
     * @param predicate the condition
     */
    void removePlayerIf(@NotNull Predicate<Player> predicate);

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
     * Checks if a string is an name of an auth server
     * @param server the server name
     * @return if the server is an auth serverr
     */
    boolean isAuthServer(@NotNull String server);
}
