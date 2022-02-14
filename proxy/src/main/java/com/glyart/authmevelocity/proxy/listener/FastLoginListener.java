package com.glyart.authmevelocity.proxy.listener;

import com.github.games647.fastlogin.velocity.event.VelocityFastLoginAutoLoginEvent;
import com.glyart.authmevelocity.proxy.AuthmeVelocityAPI;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;

public class FastLoginListener {
    private final ProxyServer server;
    private final AuthmeVelocityAPI api;
    public FastLoginListener(ProxyServer server, AuthmeVelocityAPI api){
        this.server = server;
        this.api = api;
    }
    @Subscribe
    public void onAutoLogin(VelocityFastLoginAutoLoginEvent event){
        server.getPlayer(event.getProfile().getName()).ifPresent(api::addPlayer);
    }
}
