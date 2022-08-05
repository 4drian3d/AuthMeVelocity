package me.adrianed.authmevelocity.velocity.listener;

import me.adrianed.authmevelocity.velocity.config.AuthMeConfig;
import me.adrianed.authmevelocity.velocity.config.ConfigUtils;
import me.adrianed.authmevelocity.velocity.utils.AuthmeUtils;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import com.velocitypowered.api.proxy.Player;

import me.adrianed.authmevelocity.api.velocity.AuthMeVelocityAPI;

import org.jetbrains.annotations.NotNull;

public final class ProxyListener {
    private final AuthMeConfig config;
    private final AuthMeVelocityAPI api;

    public ProxyListener(@NotNull AuthMeConfig config, AuthMeVelocityAPI api) {
        this.config = config;
        this.api = api;
    }

    @Subscribe
    public EventTask onDisconnect(final DisconnectEvent event) {
        return EventTask.async(() -> api.removePlayer(event.getPlayer()));
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onCommandExecute(final CommandExecuteEvent event, Continuation continuation) {
        if (!(event.getCommandSource() instanceof Player)){
            continuation.resume();
            return;
        }

        Player player = (Player)event.getCommandSource();

        if (api.isLogged(player)) {
            continuation.resume();
            return;
        }

        if (api.isInAuthServer(player)) {
            String command = AuthmeUtils.getFirstArgument(event.getCommand());
            if (!config.getCommandsConfig().getAllowedCommands().contains(command)) {
                ConfigUtils.sendBlockedMessage(player, config);
                event.setResult(CommandExecuteEvent.CommandResult.denied());
            }
        } else {
            ConfigUtils.sendBlockedMessage(player, config);
            event.setResult(CommandExecuteEvent.CommandResult.denied());
        }
        continuation.resume();
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerChat(final PlayerChatEvent event) {
        if (api.isNotLogged(event.getPlayer())) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onTabComplete(TabCompleteEvent event){
        if (api.isLogged(event.getPlayer())) {
            return;
        }

        final String command = event.getPartialMessage();
        for (final String allowed : config.getCommandsConfig().getAllowedCommands()) {
            if (allowed.startsWith(command)) {
                return;
            }
        }

        event.getSuggestions().clear();
    }
    
}
