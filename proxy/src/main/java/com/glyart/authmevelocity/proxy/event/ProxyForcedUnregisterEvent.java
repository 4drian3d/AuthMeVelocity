package com.glyart.authmevelocity.proxy.event;

import com.velocitypowered.api.proxy.Player;

import org.jetbrains.annotations.Nullable;

public class ProxyForcedUnregisterEvent {
    private final Player player;

    public ProxyForcedUnregisterEvent(@Nullable Player player){
        this.player = player;
    }

    public @Nullable Player getPlayer(){
        return this.player;
    }
}
