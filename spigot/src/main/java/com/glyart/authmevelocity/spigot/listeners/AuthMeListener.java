package com.glyart.authmevelocity.spigot.listeners;

import com.glyart.authmevelocity.spigot.AuthMeVelocityPlugin;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AuthMeListener implements Listener {

    private final AuthMeVelocityPlugin plugin;

    public AuthMeListener(AuthMeVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        plugin.sendLoginToProxy(event.getPlayer());
    }

}
