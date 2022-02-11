package com.glyart.authmevelocity.proxy.event;

import com.velocitypowered.api.proxy.Player;

import org.jetbrains.annotations.NotNull;

/**
 * Event executed in case the player is successfully logged in
 */
public final class ProxyLoginEvent {
    private final Player player;

    public ProxyLoginEvent(@NotNull Player player){
        this.player = player;
    }

    public @NotNull Player getPlayer(){
        return this.player;
    }
}
