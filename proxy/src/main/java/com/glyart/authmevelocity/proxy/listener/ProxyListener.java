package com.glyart.authmevelocity.proxy.listener;

import com.glyart.authmevelocity.proxy.AuthmeVelocityAPI;
import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.event.ProxyLoginEvent;
import com.google.common.io.ByteArrayDataInput;
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

import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ProxyListener {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Random rm;
    private AuthMeConfig.Config config;

    public ProxyListener(ProxyServer proxy, Logger logger, AuthMeConfig.Config config) {
        this.proxy = proxy;
        this.logger = logger;
        this.rm = new Random();
        this.config = config;
    }

    @Subscribe
    public void onPluginMessage(final PluginMessageEvent event) {
        if (!(event.getSource() instanceof ServerConnection) || !event.getIdentifier().getId().equals("authmevelocity:main"))
             return;

        ByteArrayDataInput input = event.dataAsDataStream();
        String sChannel = input.readUTF();
        if (!sChannel.equals("LOGIN")) return;

        String user = input.readUTF();
        Optional<Player> optionalPlayer = proxy.getPlayer(UUID.fromString(user));
        if (!optionalPlayer.isPresent()) return;

        Player loggedPlayer = optionalPlayer.get();
        if (AuthmeVelocityAPI.addPlayer(loggedPlayer)){
            RegisteredServer loginServer = loggedPlayer.getCurrentServer().orElseThrow().getServer();
            proxy.getEventManager().fireAndForget(new ProxyLoginEvent(loggedPlayer, loginServer));
            if(config.getToServerOptions().sendToServer()){
                List<String> serverList = config.getToServerOptions().getTeleportServers();
                String randomServer = serverList.get(rm.nextInt(serverList.size()));
                Optional<RegisteredServer> optionalServer = proxy.getServer(randomServer);
                optionalServer.ifPresentOrElse(serverToSend -> {
                    try{
                        if(!loggedPlayer.createConnectionRequest(serverToSend).connect().get().isSuccessful()){
                            logger.info("Unable to connect the player {} to the server {}",
                                loggedPlayer.getUsername(),
                                serverToSend.getServerInfo().getName());
                        }
                    } catch (InterruptedException | ExecutionException exception){
                        logger.info("Unable to connect the player {} to the server {}. Error: {}",
                            loggedPlayer.getUsername(),
                            serverToSend.getServerInfo().getName(),
                            exception);
                    }
                }, () -> logger.info("The server {} does not exist", randomServer));
            }
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
    public void onPlayerChat(final PlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (AuthmeVelocityAPI.isLogged(player)) return;

        Optional<ServerConnection> server = player.getCurrentServer();
        if (server.isPresent() && config.getAuthServers().contains(server.get().getServerInfo().getName())) {
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
        final Player player = event.getPlayer();
        if (!AuthmeVelocityAPI.isLogged(player)){
            return EventTask.async(() -> event.getSuggestions().clear());
        }
        return null;
    }
}
