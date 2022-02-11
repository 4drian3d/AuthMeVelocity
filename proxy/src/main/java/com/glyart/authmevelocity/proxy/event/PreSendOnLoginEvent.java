package com.glyart.authmevelocity.proxy.event;

import java.util.Objects;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.ResultedEvent.GenericResult;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

/**
 * Event to be executed just before sending a player to another server after login/registration.
 * Here you have the ability to deny the event.
 */
public final class PreSendOnLoginEvent implements ResultedEvent<GenericResult> {

    private GenericResult result = GenericResult.allowed();
    private final Player player;
    private final RegisteredServer actualserver;
    private final RegisteredServer serverToSend;

    /**
     * Create a new PreSendOnLoginEvent
     * @param player the player logged
     * @param actualServer the server on which the player is located
     * @param serverToSend the server to which the player will be sent
     */
    public PreSendOnLoginEvent(Player player, RegisteredServer actualServer, RegisteredServer serverToSend){
        this.player = player;
        this.actualserver = actualServer;
        this.serverToSend = serverToSend;
    }

    /**
     * Obtain the logged player
     * @return the player
     */
    public Player getPlayer(){
        return this.player;
    }

    /**
     * Obtain the server on which the player is located
     * @return the actual server of the player
     */
    public RegisteredServer getActualServer(){
        return this.actualserver;
    }

    /**
     * Obtain the server to which the player will be sent
     * @return the server to send the player
     */
    public RegisteredServer getSendServer(){
        return this.serverToSend;
    }

    /**
     * Get the result of the event
     */
    @Override
    public GenericResult getResult() {
        return this.result;
    }

    /**
     * Set the result of the event
     * @param newresult the new result
     */
    @Override
    public void setResult(GenericResult newresult) {
        this.result = Objects.requireNonNull(newresult);
    }
}
