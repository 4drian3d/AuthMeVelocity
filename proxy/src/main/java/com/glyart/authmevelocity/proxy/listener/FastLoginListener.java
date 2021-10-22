package com.glyart.authmevelocity.proxy.listener;

import java.util.Optional;
import java.util.UUID;

import com.github.games647.fastlogin.velocity.event.VelocityFastLoginAutoLoginEvent;
import com.glyart.authmevelocity.proxy.AuthMeVelocityPlugin;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

public class FastLoginListener {
    private final AuthMeVelocityPlugin plugin;
    private final ProxyServer server;
    public FastLoginListener(AuthMeVelocityPlugin plugin, ProxyServer server){
        this.plugin = plugin;
        this.server = server;
    }
    @Subscribe
    public void onAutoLogin(VelocityFastLoginAutoLoginEvent event){
        Optional<Player> autoLoginPlayer = server.getPlayer(event.getProfile().getName());
        if(autoLoginPlayer.isPresent()){
            UUID playerUUID = autoLoginPlayer.get().getUniqueId();
            if(!plugin.loggedPlayers.contains(playerUUID)){
                plugin.loggedPlayers.add(playerUUID);
            }
        }
    }
}
