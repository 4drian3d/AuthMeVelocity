package me.adrianed.authmevelocity.velocity.listener;

import com.github.games647.fastlogin.velocity.event.VelocityFastLoginAutoLoginEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;

import me.adrianed.authmevelocity.api.velocity.AuthMeVelocityAPI;

public class FastLoginListener {
    private final ProxyServer proxy;
    private final AuthMeVelocityAPI api;
    public FastLoginListener(ProxyServer proxy, AuthMeVelocityAPI api){
        this.proxy = proxy;
        this.api = api;
    }
    @Subscribe
    public void onAutoLogin(VelocityFastLoginAutoLoginEvent event){
        proxy.getPlayer(event.getProfile().getName())
        .ifPresent(api::addPlayer);
    }
}
