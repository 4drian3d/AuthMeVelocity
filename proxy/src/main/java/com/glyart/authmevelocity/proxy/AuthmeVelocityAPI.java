package com.glyart.authmevelocity.proxy;

import java.util.UUID;
import java.util.function.Predicate;

import com.velocitypowered.api.proxy.Player;

import org.jetbrains.annotations.NotNull;

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

    private AuthmeVelocityAPI(){}
}
