package com.glyart.authmevelocity.proxy;

import java.util.function.Predicate;

import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import org.jetbrains.annotations.NotNull;

/**
 * API provided to interact with logged players
 */
public final class AuthmeVelocityAPI {
    private final AuthMeVelocityPlugin plugin;
    private final AuthMeConfig config;
    AuthmeVelocityAPI(AuthMeVelocityPlugin plugin, AuthMeConfig config){
        this.plugin = plugin;
        this.config = config;
    }

    /**
     * Check if the player is logged in or not
     * @param player the player
     * @return if the player is logged in or not
     */
    public boolean isLogged(@NotNull Player player){
        return plugin.loggedPlayers.contains(player.getUniqueId());
    }

    /**
     * Check if the player is not logged
     * @param player the player
     * @return if the player is not logged
     */
    public boolean isNotLogged(@NotNull Player player){
        return !plugin.loggedPlayers.contains(player.getUniqueId());
    }

    /**
     * Adds a player to the list of logged in players
     * @param player the new logged player
     * @return if the player was succesfully added
     */
    public boolean addPlayer(@NotNull Player player){
        return plugin.loggedPlayers.add(player.getUniqueId());
    }

    /**
     * Removes a player from the list of logged-in players
     * @param player the unlogged player
     * @return if the player was succesfully removed
     */
    public boolean removePlayer(@NotNull Player player){
        return plugin.loggedPlayers.remove(player.getUniqueId());
    }

    /**
     * Removes players who meet the established condition
     * @param predicate the condition
     */
    public void removePlayerIf(@NotNull Predicate<Player> predicate){
        plugin.loggedPlayers.removeIf(uuid -> predicate.test(plugin.getProxy().getPlayer(uuid).orElse(null)));
    }

    /**
     * Check if the player is on a login server
     * @param player the player
     * @return if the player is on a login server
     */
    public boolean isInAuthServer(@NotNull Player player){
        return player.getCurrentServer().map(this::isAuthServer).orElse(false);
    }

    /**
     * Check if a server is intended to be a logging server
     * @param server the server
     * @return if the server is a login server
     */
    public boolean isAuthServer(@NotNull RegisteredServer server){
        return config.getAuthServers().contains(server.getServerInfo().getName());
    }

    /**
     * Checks if a connection is made from a login server
     * @param connection the connection
     * @return if the connection is made from a login server
     */
    public boolean isAuthServer(@NotNull ServerConnection connection){
        return config.getAuthServers().contains(connection.getServerInfo().getName());
    }

    /**
     * Checks if a string is an name of an auth server
     * @param server the server name
     * @return if the server is an auth serverr
     */
    public boolean isAuthServer(@NotNull String server){
        return config.getAuthServers().contains(server);
    }
}
