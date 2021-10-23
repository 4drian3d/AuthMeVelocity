package com.glyart.authmevelocity.proxy.event;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

/**
 * Event executed in case the player is successfully logged in
 */
public class ProxyLoginEvent {

    private final Player player;
    private final RegisteredServer server;

    public ProxyLoginEvent(Player player, RegisteredServer server){
        this.player = player;
        this.server = server;
    }

    /**
     * Get the player who has logged in
     * @return the login player
     */
    public Player getPlayer(){
        return this.player;
    }

    /**
     * Obtain the server to which the user is logged in.
     * @return the login server
     */
    public RegisteredServer getServer(){
        return this.server;
    }
}
