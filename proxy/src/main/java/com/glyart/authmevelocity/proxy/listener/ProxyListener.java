package com.glyart.authmevelocity.proxy.listener;

import java.util.Optional;

import com.glyart.authmevelocity.proxy.AuthMeVelocityPlugin;
import com.glyart.authmevelocity.proxy.AuthmeVelocityAPI;
import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.config.ConfigUtils;
import com.glyart.authmevelocity.proxy.utils.AuthmeUtils;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class ProxyListener {
    private final AuthMeConfig config;
    private final AuthmeVelocityAPI api;
    private final ProxyServer proxy;
    private final Logger logger;

    public ProxyListener(@NotNull AuthMeConfig config, AuthmeVelocityAPI api, Logger logger, ProxyServer proxy) {
        this.config = config;
        this.api = api;
        this.logger = logger;
        this.proxy = proxy;
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

    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerChat(final PlayerChatEvent event) {
        if (!api.isLogged(event.getPlayer())) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event, Continuation continuation) {
        if (!event.getResult().isAllowed() && api.isLogged(event.getPlayer())){
            continuation.resume();
            return;
        }
        if(!event.getResult().getServer().map(api::isAuthServer).orElse(false)){
            continuation.resume();
            return;
        }
        event.setResult(ServerPreConnectEvent.ServerResult.denied());
        continuation.resume();
    }

    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        Player player = event.getPlayer();
        if(api.isInAuthServer(player)){
            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            buf.writeUTF("LOGIN");
            player.sendPluginMessage(AuthMeVelocityPlugin.AUTHMEVELOCITY_CHANNEL, buf.toByteArray());
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public EventTask onTabComplete(TabCompleteEvent event){
        return EventTask.async(() -> {
            if (!api.isLogged(event.getPlayer())){
                event.getSuggestions().clear();
            }
        });
    }

    @Subscribe(order = PostOrder.LATE)
    public void onInitialServer(PlayerChooseInitialServerEvent event, Continuation continuation){
        if(!config.getEnsureOptions().ensureAuthServer()){
            continuation.resume();
            return;
        }
        if(event.getInitialServer().map(api::isAuthServer).orElse(false)){
            continuation.resume();
            return;
        }
        @Nullable RegisteredServer server = getAvailableServer();
        if(server == null) {
            continuation.resume();
            logger.error("Cannot send the player {} to an auth server", event.getPlayer().getUsername());
            String disconnectMessage = config.getEnsureOptions().getDisconnectMessage();
            event.getPlayer().disconnect(ConfigUtils.MINIMESSAGE.deserialize(disconnectMessage));
            return;
        }
        event.setInitialServer(server);
        continuation.resume();

    }

    private @Nullable RegisteredServer getAvailableServer(){
        for(String sv : config.getAuthServers()){
            Optional<RegisteredServer> opt = proxy.getServer(sv);
            if(opt.isPresent()) return opt.get();
        }
        return null;
    }
}
