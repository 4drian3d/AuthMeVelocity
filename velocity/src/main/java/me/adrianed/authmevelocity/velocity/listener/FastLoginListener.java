package me.adrianed.authmevelocity.velocity.listener;

import com.github.games647.fastlogin.velocity.event.VelocityFastLoginAutoLoginEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;

import me.adrianed.authmevelocity.velocity.AuthMeVelocityPlugin;

public class FastLoginListener {
    private final ProxyServer proxy;
    private final AuthMeVelocityPlugin plugin;
    public FastLoginListener(ProxyServer proxy, AuthMeVelocityPlugin plugin){
        this.proxy = proxy;
        this.plugin = plugin;
    }
    @Subscribe
    public void onAutoLogin(VelocityFastLoginAutoLoginEvent event){
        plugin.logDebug("VelocityFastLoginAutoLoginEvent | Attempt to auto register player");
        proxy.getPlayer(event.getProfile().getName()).ifPresent(plugin::addPlayer);
    }
}
