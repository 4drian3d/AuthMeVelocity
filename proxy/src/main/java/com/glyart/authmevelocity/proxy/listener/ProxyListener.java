package com.glyart.authmevelocity.proxy.listener;

import com.glyart.authmevelocity.proxy.AuthmeVelocityAPI;
import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.config.ConfigUtils;
import com.glyart.authmevelocity.proxy.utils.AuthmeUtils;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import com.velocitypowered.api.proxy.Player;

import org.jetbrains.annotations.NotNull;

public final class ProxyListener {
    private final AuthMeConfig config;
    private final AuthmeVelocityAPI api;

    public ProxyListener(@NotNull AuthMeConfig config, AuthmeVelocityAPI api) {
        this.config = config;
        this.api = api;
    }

    @Subscribe
    public void onDisconnect(final DisconnectEvent event) {
        api.removePlayer(event.getPlayer());
    }

    @Subscribe
    public void onCommandExecute(final CommandExecuteEvent event, Continuation continuation) {
        if (!(event.getCommandSource() instanceof Player)){
            continuation.resume();
            return;
        }

        Player player = ((Player)event.getCommandSource());

        if(api.isLogged(player)){
            continuation.resume();
            return;
        }

        if(api.isInAuthServer(player)){
            String command = AuthmeUtils.getFirstArgument(event.getCommand());
            if(!config.getCommandsConfig().getAllowedCommands().contains(command)){
                ConfigUtils.sendBlockedMessage(player, config);
                event.setResult(CommandExecuteEvent.CommandResult.denied());
            }
        } else {
            ConfigUtils.sendBlockedMessage(player, config);
            event.setResult(CommandExecuteEvent.CommandResult.denied());
        }
        continuation.resume();
    }

    @Subscribe
    public void onPlayerChat(final PlayerChatEvent event) {
        if (!api.isLogged(event.getPlayer())) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event, Continuation continuation) {
        if (api.isLogged(event.getPlayer())){
            continuation.resume();
            return;
        }

        event.getResult().getServer().ifPresent(server -> {
            if(!api.isAuthServer(server)){
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
            }
        });
        continuation.resume();
    }

    @Subscribe
    public EventTask onTabComplete(TabCompleteEvent event){
        if (!api.isLogged(event.getPlayer())){
            return EventTask.async(() -> event.getSuggestions().clear());
        }
        return null;
    }
}
