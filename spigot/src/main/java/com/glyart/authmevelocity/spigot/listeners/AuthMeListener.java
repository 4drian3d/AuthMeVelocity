package com.glyart.authmevelocity.spigot.listeners;

import com.glyart.authmevelocity.spigot.AuthMeVelocityPlugin;
import com.glyart.authmevelocity.spigot.MessageType;
import com.glyart.authmevelocity.spigot.events.PreSendLoginEvent;

import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.LogoutEvent;
import fr.xephi.authme.events.RegisterEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AuthMeListener implements Listener {
    private final AuthMeVelocityPlugin plugin;

    public AuthMeListener(AuthMeVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(final LoginEvent event) {
        final Player player = event.getPlayer();
        PreSendLoginEvent preSendLoginEvent = new PreSendLoginEvent(player);
        Bukkit.getPluginManager().callEvent(preSendLoginEvent);
        if(!preSendLoginEvent.isCancelled()){
            plugin.sendMessageToProxy(player, MessageType.LOGIN);
        }
    }

    @EventHandler
    public void onRegister(RegisterEvent event){
        plugin.sendMessageToProxy(event.getPlayer(), MessageType.REGISTER);
    }

    @EventHandler
    public void onLogout(LogoutEvent event){
        plugin.sendMessageToProxy(event.getPlayer(), MessageType.LOGOUT);
    }
}
