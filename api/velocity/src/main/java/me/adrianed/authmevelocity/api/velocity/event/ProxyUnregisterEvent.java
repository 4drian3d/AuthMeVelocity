package me.adrianed.authmevelocity.api.velocity.event;

import com.velocitypowered.api.proxy.Player;

import org.jetbrains.annotations.NotNull;

public class ProxyUnregisterEvent {
    private final Player player;

    public ProxyUnregisterEvent(@NotNull Player player){
        this.player = player;
    }

    public @NotNull Player getPlayer(){
        return this.player;
    }
}
