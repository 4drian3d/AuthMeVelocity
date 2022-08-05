package me.adrianed.authmevelocity.api.velocity.event;

import com.velocitypowered.api.proxy.Player;

import org.jetbrains.annotations.NotNull;

public final class ProxyLogoutEvent {
    private final Player player;

    public ProxyLogoutEvent(@NotNull Player player){
        this.player = player;
    }

    public @NotNull Player getPlayer(){
        return this.player;
    }
}
