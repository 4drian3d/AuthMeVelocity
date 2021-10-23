package com.glyart.authmevelocity.proxy.listener;

import com.glyart.authmevelocity.proxy.AuthMeVelocityPlugin;
import com.glyart.authmevelocity.proxy.AuthmeVelocityAPI;
import com.glyart.authmevelocity.proxy.event.ProxyLoginEvent;
import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
//import com.velocitypowered.api.event.player.TabCompleteEvent;
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
    private final ProxyServer server;
    private final Logger logger;

    public ProxyListener(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onPluginMessage(final PluginMessageEvent event) {
        if (!(event.getSource() instanceof ServerConnection)) return;

        if (!event.getIdentifier().getId().equals("authmevelocity:main")) return;

        ByteArrayDataInput input = event.dataAsDataStream();
        String sChannel = input.readUTF();
        if (!sChannel.equals("LOGIN")) return;

        String user = input.readUTF();
        Optional<Player> optionalPlayer = server.getPlayer(UUID.fromString(user));
        if (!optionalPlayer.isPresent()) return;

        Player loggedPlayer = optionalPlayer.get();
        if (!AuthmeVelocityAPI.isLogged(loggedPlayer)){
            AuthmeVelocityAPI.addPlayer(loggedPlayer);

            RegisteredServer loginServer = loggedPlayer.getCurrentServer().get().getServer();
            server.getEventManager().fireAndForget(new ProxyLoginEvent(loggedPlayer, loginServer));
            if(AuthMeVelocityPlugin.getConfig().getBoolean("teleport.send-to-server-after-login")){
                Random rm = new Random();
                List<String> serverList = AuthMeVelocityPlugin.getConfig().getStringList("teleport.servers");
                String randomServer = serverList.get(rm.nextInt(serverList.size()));
                Optional<RegisteredServer> optionalServer = server.getServer(randomServer);
                if(optionalServer.isPresent()){
                    RegisteredServer serverToSend = optionalServer.get();
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
                } else{
                    logger.info("The server {} does not exist", randomServer);
                }
            }
        }
    }

    @Subscribe
    public void onDisconnect(final DisconnectEvent event) {
        AuthmeVelocityAPI.removePlayer(event.getPlayer());
    }

    @Subscribe
    public void onCommandExecute(final CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player player)) return;

        if (AuthmeVelocityAPI.isLogged(player)) return;

        Optional<ServerConnection> server = player.getCurrentServer();
        boolean isAuthServer = server.isPresent() &&
            AuthMeVelocityPlugin.getConfig().getList("authservers").contains(server.get().getServerInfo().getName());

        if (isAuthServer) {
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
        if (server.isPresent() && AuthMeVelocityPlugin.getConfig().getList("authservers").contains(server.get().getServerInfo().getName())) {
            return;
        }

        event.setResult(PlayerChatEvent.ChatResult.denied());
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
        if (AuthmeVelocityAPI.isLogged(event.getPlayer())) return;

        Optional<RegisteredServer> server = event.getResult().getServer();
        if (server.isPresent() && AuthMeVelocityPlugin.getConfig().getList("authservers").contains(server.get().getServerInfo().getName())) {
            return;
        }

        event.setResult(ServerPreConnectEvent.ServerResult.denied());
    }

    /*
    "You have the opportunity to modify the response sent to the remote player."
    In theory... it could be modified, but the respective methods do not exist.
    I hope that the other event works for <1.12 even though this one should work.
    */

    /*@Subscribe
    public void onTabComplete(TabCompleteEvent event){
        Player player = event.getPlayer();
        if (plugin.loggedPlayers.contains(player.getUniqueId())) return;
        event.setTabComplete();?
    }*/

    @Subscribe
    public void onTabComplete(PlayerAvailableCommandsEvent event){
        if (!AuthmeVelocityAPI.isLogged(event.getPlayer())) {
            event.getRootNode().getChildren().iterator().remove();
        }
    }
}
