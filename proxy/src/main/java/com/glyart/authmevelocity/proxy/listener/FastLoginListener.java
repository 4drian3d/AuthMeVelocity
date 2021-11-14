package com.glyart.authmevelocity.proxy.listener;

import java.util.Optional;

import com.github.games647.fastlogin.velocity.event.VelocityFastLoginAutoLoginEvent;
import com.glyart.authmevelocity.proxy.AuthmeVelocityAPI;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

public class FastLoginListener {
    private final ProxyServer server;
    public FastLoginListener(ProxyServer server){
        this.server = server;
    }
    @Subscribe
    public void onAutoLogin(VelocityFastLoginAutoLoginEvent event){
        Optional<Player> autoLoginPlayer = server.getPlayer(event.getProfile().getName());
        autoLoginPlayer.ifPresent(AuthmeVelocityAPI::addPlayer);
    }
}
