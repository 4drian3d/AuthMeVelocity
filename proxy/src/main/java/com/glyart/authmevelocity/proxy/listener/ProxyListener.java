package com.glyart.authmevelocity.proxy.listener;

import com.glyart.authmevelocity.proxy.AuthmeVelocityAPI;
import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.config.ConfigUtils;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import com.velocitypowered.api.proxy.Player;

import org.jetbrains.annotations.NotNull;

public class ProxyListener {
    private AuthMeConfig.Config config;

    public ProxyListener(@NotNull AuthMeConfig.Config config) {
        this.config = config;
    }

    @Subscribe
    public void onDisconnect(final DisconnectEvent event) {
        AuthmeVelocityAPI.removePlayer(event.getPlayer());
    }

    @Subscribe
    public void onCommandExecute(final CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player player) || AuthmeVelocityAPI.isLogged(player))
            return;

        if(AuthmeVelocityAPI.isInAuthServer(player)){
            var commandconfig = config.getCommandsConfig();
            String command = event.getCommand();
            if(command.contains(" ")){
                command = command.split(" ")[0];
            }
            if(!commandconfig.getAllowedCommands().contains(command)){
                ConfigUtils.sendBlockedMessage(player);
                event.setResult(CommandExecuteEvent.CommandResult.denied());
            }
        } else {
            ConfigUtils.sendBlockedMessage(player);
            event.setResult(CommandExecuteEvent.CommandResult.denied());
        }
    }

    @Subscribe
    public void onPlayerChat(final PlayerChatEvent event) {
        if (!AuthmeVelocityAPI.isLogged(event.getPlayer())) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
        if (AuthmeVelocityAPI.isLogged(event.getPlayer())) return;

        event.getResult().getServer().ifPresent(server -> {
            if(!AuthmeVelocityAPI.isAuthServer(server)){
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
            }
        });
    }

    @Subscribe
    public EventTask onTabComplete(TabCompleteEvent event){
        if (!AuthmeVelocityAPI.isLogged(event.getPlayer())){
            return EventTask.async(() -> event.getSuggestions().clear());
        }
        return null;
    }
}
