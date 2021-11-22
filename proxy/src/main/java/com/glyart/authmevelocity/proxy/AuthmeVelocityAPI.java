package com.glyart.authmevelocity.proxy;

import java.util.UUID;
import java.util.function.Predicate;

import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import org.jetbrains.annotations.NotNull;

/**
 * APi provided to interact with logged players
 */
public class AuthmeVelocityAPI {
    /**
     * Check if the player is logged in or not
     * @param player the player
     * @return if the player is logged in or not
     */
    public static boolean isLogged(@NotNull Player player){
        final UUID playerUUID = player.getUniqueId();
        return AuthMeVelocityPlugin.loggedPlayers.contains(playerUUID);
    }

    /**
     * Adds a player to the list of logged in players
     * @param player the new logged player
     * @return if the player was succesfully added
     */
    public static boolean addPlayer(@NotNull Player player){
        final UUID playerUUID = player.getUniqueId();
        return AuthMeVelocityPlugin.loggedPlayers.add(playerUUID);
    }

    /**
     * Removes a player from the list of logged-in players
     * @param player the unlogged player
     * @return if the player was succesfully removed
     */
    public static boolean removePlayer(@NotNull Player player){
        final UUID playerUUID = player.getUniqueId();
        return AuthMeVelocityPlugin.loggedPlayers.remove(playerUUID);
    }

    /**
     * Removes players who meet the established condition
     * @param predicate the condition
     */
    public static void removePlayerIf(@NotNull Predicate<Player> predicate){
        AuthMeVelocityPlugin.loggedPlayers.stream()
            .map(uuid -> AuthMeVelocityPlugin.getInstance().getProxy().getPlayer(uuid).orElseThrow())
            .filter(predicate)
            .forEach(player -> AuthMeVelocityPlugin.loggedPlayers.remove(player.getUniqueId()));
    }

    /**
     * Check if the player is on a login server
     * @param player the player
     * @return if the player is on a login server
     */
    public static boolean isInAuthServer(@NotNull Player player){
        var connection = player.getCurrentServer();
        return connection.isPresent() && isAuthServer(connection.get());
    }

    /**
     * Check if a server is intended to be a logging server
     * @param server the server
     * @return if the server is a login server
     */
    public static boolean isAuthServer(@NotNull RegisteredServer server){
        return AuthMeConfig.getConfig().getAuthServers().contains(server.getServerInfo().getName());
    }

    /**
     * Checks if a connection is made from a login server
     * @param connection the connection
     * @return if the connection is made from a login server
     */
    public static boolean isAuthServer(@NotNull ServerConnection connection){
        return AuthMeConfig.getConfig().getAuthServers().contains(connection.getServerInfo().getName());
    }

    private AuthmeVelocityAPI(){}
}
