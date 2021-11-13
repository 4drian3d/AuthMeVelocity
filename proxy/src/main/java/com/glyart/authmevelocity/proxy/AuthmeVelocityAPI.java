package com.glyart.authmevelocity.proxy;

import java.util.UUID;
import java.util.function.Predicate;

import com.velocitypowered.api.proxy.Player;

public class AuthmeVelocityAPI {
    /**
     * Check if the player is logged in or not
     * @param player the player
     * @return if the player is logged in or not
     */
    public static boolean isLogged(Player player){
        final UUID playerUUID = player.getUniqueId();
        return AuthMeVelocityPlugin.loggedPlayers.contains(playerUUID);
    }

    /**
     * Adds a player to the list of logged in players
     * @param player the new logged player
     */
    public static void addPlayer(Player player){
        final UUID playerUUID = player.getUniqueId();
        if(!AuthmeVelocityAPI.isLogged(player)){
            AuthMeVelocityPlugin.loggedPlayers.add(playerUUID);
        }
    }

    /**
     * Removes a player from the list of logged-in players
     * @param player the unlogged player
     */
    public static void removePlayer(Player player){
        final UUID playerUUID = player.getUniqueId();
        if(AuthmeVelocityAPI.isLogged(player)){
            AuthMeVelocityPlugin.loggedPlayers.remove(playerUUID);
        }
    }

    /**
     * Removes players who meet the established condition
     * @param predicate the condition
     */
    public static void removePlayerIf(Predicate<Player> predicate){
        AuthMeVelocityPlugin.loggedPlayers.stream()
            .map(uuid -> AuthMeVelocityPlugin.getProxy().getPlayer(uuid).orElse(null))
            .filter(predicate)
            .forEach(player -> AuthMeVelocityPlugin.loggedPlayers.remove(player.getUniqueId()));
    }

    private AuthmeVelocityAPI(){}
}
