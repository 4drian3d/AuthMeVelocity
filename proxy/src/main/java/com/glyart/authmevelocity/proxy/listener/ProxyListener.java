package com.glyart.authmevelocity.proxy.listener;

import com.glyart.authmevelocity.proxy.AuthmeVelocityAPI;
import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.event.PreSendOnLoginEvent;
import com.glyart.authmevelocity.proxy.event.ProxyLoginEvent;
import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ProxyListener {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Random rm;
    private AuthMeConfig.Config config;

    public ProxyListener(@NotNull ProxyServer proxy, @NotNull Logger logger, @NotNull AuthMeConfig.Config config) {
        this.proxy = proxy;
        this.logger = logger;
        this.rm = new Random();
        this.config = config;
    }

    @Subscribe
    public void onPluginMessage(final PluginMessageEvent event, Continuation continuation) {
        if (!(event.getSource() instanceof ServerConnection connection) || !event.getIdentifier().getId().equals("authmevelocity:main")){
            continuation.resume();
            return;
        }

        ByteArrayDataInput input = event.dataAsDataStream();
        final String sChannel = input.readUTF();
        if (!sChannel.equals("LOGIN")) {
            continuation.resume();
            return;
        }

        event.setResult(PluginMessageEvent.ForwardResult.handled());
        //TODO: Test this
        final Player loggedPlayer = connection.getPlayer();
        if (AuthmeVelocityAPI.addPlayer(loggedPlayer)){
            createServerConnectionRequest(loggedPlayer, config, proxy, logger, connection);
            continuation.resume();
        }
    }

    private void createServerConnectionRequest(Player loggedPlayer, AuthMeConfig.Config config, ProxyServer proxy, Logger logger, ServerConnection connection){
        final RegisteredServer loginServer = loggedPlayer.getCurrentServer().orElse(connection).getServer();
        proxy.getEventManager().fireAndForget(new ProxyLoginEvent(loggedPlayer, loginServer));
        if(config.getToServerOptions().sendToServer()){
            final List<String> serverList = config.getToServerOptions().getTeleportServers();
            final String randomServer = serverList.get(rm.nextInt(serverList.size()));
            Optional<RegisteredServer> optionalServer = proxy.getServer(randomServer);
            optionalServer.ifPresentOrElse(serverToSend ->
                proxy.getEventManager().fire(new PreSendOnLoginEvent(loggedPlayer, loginServer, serverToSend)).thenAcceptAsync(preSendEvent -> {
                    if(preSendEvent.getResult().isAllowed()){
                        loggedPlayer.createConnectionRequest(serverToSend).connect().thenAcceptAsync(result -> {
                            if(!result.isSuccessful()) {
                                logger.info("Unable to connect the player {} to the server {}",
                                    loggedPlayer.getUsername(),
                                    serverToSend.getServerInfo().getName());
                            }
                        });
                    }
                })
            , () -> logger.info("The server {} does not exist", randomServer));
        }
    }

    @Subscribe
    public void onDisconnect(final DisconnectEvent event) {
        AuthmeVelocityAPI.removePlayer(event.getPlayer());
    }

    @Subscribe
    public void onCommandExecute(final CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player player) || AuthmeVelocityAPI.isLogged(player))
            return;

        Optional<ServerConnection> server = player.getCurrentServer();

        if (server.isPresent() && config.getAuthServers().contains(server.get().getServerInfo().getName())) {
            event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
        } else {
            event.setResult(CommandExecuteEvent.CommandResult.denied());
        }
    }

    @Subscribe
    public void onPlayerChat(final PlayerChatEvent event, Continuation continuation) {
        final Player player = event.getPlayer();
        if (AuthmeVelocityAPI.isLogged(player)) {
            continuation.resume();
            return;
        }

        Optional<ServerConnection> server = player.getCurrentServer();
        if (server.isPresent() && config.getAuthServers().contains(server.get().getServerInfo().getName())) {
            continuation.resume();
            return;
        }

        event.setResult(PlayerChatEvent.ChatResult.denied());
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
        if (AuthmeVelocityAPI.isLogged(event.getPlayer())) return;

        Optional<RegisteredServer> server = event.getResult().getServer();
        if (server.isPresent() && config.getAuthServers().contains(server.get().getServerInfo().getName())) {
            return;
        }

        event.setResult(ServerPreConnectEvent.ServerResult.denied());
    }

    @Subscribe
    public EventTask onTabComplete(TabCompleteEvent event){
        if (!AuthmeVelocityAPI.isLogged(event.getPlayer())){
            return EventTask.async(() -> event.getSuggestions().clear());
        }
        return null;
    }
}
