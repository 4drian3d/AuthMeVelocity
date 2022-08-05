package me.adrianed.authmevelocity.paper.listeners;

import me.adrianed.authmevelocity.paper.AuthMeVelocityPlugin;
import me.adrianed.authmevelocity.common.MessageType;
import me.adrianed.authmevelocity.api.paper.event.PreSendLoginEvent;

import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.LogoutEvent;
import fr.xephi.authme.events.RegisterEvent;
import fr.xephi.authme.events.UnregisterByAdminEvent;
import fr.xephi.authme.events.UnregisterByPlayerEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class AuthMeListener implements Listener {
    private final AuthMeVelocityPlugin plugin;

    public AuthMeListener(AuthMeVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(LoginEvent event) {
        final Player player = event.getPlayer();

        if (new PreSendLoginEvent(player).callEvent()) {
            plugin.sendMessageToProxy(player, MessageType.LOGIN, player.getName());
        }
    }

    @EventHandler
    public void onRegister(RegisterEvent event){
        plugin.sendMessageToProxy(event.getPlayer(), MessageType.REGISTER, event.getPlayer().getName());
    }

    @EventHandler
    public void onLogout(LogoutEvent event){
        plugin.sendMessageToProxy(event.getPlayer(), MessageType.LOGOUT, event.getPlayer().getName());
    }

    @EventHandler
    public void onUnRegister(UnregisterByPlayerEvent event){
        plugin.sendMessageToProxy(event.getPlayer(), MessageType.UNREGISTER, event.getPlayer().getName());
    }

    @EventHandler
    public void onAdminUnRegister(UnregisterByAdminEvent event){
        plugin.sendMessageToProxy(event.getPlayer(), MessageType.FORCE_UNREGISTER, event.getPlayerName());
    }
}
