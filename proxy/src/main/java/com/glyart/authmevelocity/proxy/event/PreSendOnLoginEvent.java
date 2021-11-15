package com.glyart.authmevelocity.proxy.event;

import java.util.Objects;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.ResultedEvent.GenericResult;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public class PreSendOnLoginEvent implements ResultedEvent<GenericResult> {

    private GenericResult result = GenericResult.allowed();
    private final Player player;
    private final RegisteredServer actualserver;
    private final RegisteredServer serverToSend;

    public PreSendOnLoginEvent(Player player, RegisteredServer actualServer, RegisteredServer serverToSend){
        this.player = player;
        this.actualserver = actualServer;
        this.serverToSend = serverToSend;
    }

    public Player getPlayer(){
        return this.player;
    }

    public RegisteredServer getActualServer(){
        return this.actualserver;
    }

    public RegisteredServer getSendServer(){
        return this.serverToSend;
    }
    
    @Override
    public GenericResult getResult() {
        return this.result;
    }

    @Override
    public void setResult(GenericResult newresult) {
        this.result = Objects.requireNonNull(newresult);
    }
}
