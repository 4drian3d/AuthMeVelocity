package me.adrianed.authmevelocity.velocity.listener;

import com.github.games647.fastlogin.velocity.event.VelocityFastLoginAutoLoginEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;

import me.adrianed.authmevelocity.velocity.AuthMeVelocityAPI;

public class FastLoginListener {
    private final ProxyServer server;
    private final AuthMeVelocityAPI api;
    public FastLoginListener(ProxyServer server, AuthMeVelocityAPI api){
        this.server = server;
        this.api = api;
    }
    @Subscribe
    public void onAutoLogin(VelocityFastLoginAutoLoginEvent event){
        server.getPlayer(event.getProfile().getName()).ifPresent(api::addPlayer);
    }
}
