package com.glyart.authmevelocity.proxy.listener;

import java.util.List;
import java.util.Random;

import com.glyart.authmevelocity.proxy.AuthMeVelocityPlugin;
import com.glyart.authmevelocity.proxy.AuthmeVelocityAPI;
import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.glyart.authmevelocity.proxy.event.PreSendOnLoginEvent;
import com.glyart.authmevelocity.proxy.event.ProxyForcedUnregisterEvent;
import com.glyart.authmevelocity.proxy.event.ProxyLoginEvent;
import com.glyart.authmevelocity.proxy.event.ProxyLogoutEvent;
import com.glyart.authmevelocity.proxy.event.ProxyRegisterEvent;
import com.glyart.authmevelocity.proxy.event.ProxyUnregisterEvent;
import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class PluginMessageListener {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Random rm;
    private final AuthMeConfig config;
    private final AuthmeVelocityAPI api;

    public PluginMessageListener(@NotNull ProxyServer proxy, @NotNull Logger logger, @NotNull AuthMeConfig config, AuthmeVelocityAPI api) {
        this.proxy = proxy;
        this.logger = logger;
        this.rm = new Random();
        this.config = config;
        this.api = api;
    }

    @Subscribe
    public void onPluginMessage(final PluginMessageEvent event, Continuation continuation) {
        if (!(event.getSource() instanceof ServerConnection) || !event.getIdentifier().equals(AuthMeVelocityPlugin.AUTHMEVELOCITY_CHANNEL)){
            continuation.resume();
            return;
        }
        ServerConnection connection = ((ServerConnection)event.getSource());

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        final ByteArrayDataInput input = event.dataAsDataStream();
        final String sChannel = input.readUTF();
        final String playername = input.readUTF();
        final @Nullable Player loggedPlayer = proxy.getPlayer(playername).orElse(null);
        switch(sChannel){
            case "LOGIN" :
                if (loggedPlayer != null && api.addPlayer(loggedPlayer)){
                    this.createServerConnectionRequest(loggedPlayer, connection);
                }
                break;
            case "LOGOUT":
                if(loggedPlayer != null && api.removePlayer(loggedPlayer)){
                    proxy.getEventManager().fireAndForget(new ProxyLogoutEvent(loggedPlayer));
                }
                break;
            case "REGISTER":
                if(loggedPlayer != null)
                    proxy.getEventManager().fireAndForget(new ProxyRegisterEvent(loggedPlayer));
                break;
            case "UNREGISTER":
                if(loggedPlayer != null)
                    proxy.getEventManager().fireAndForget(new ProxyUnregisterEvent(loggedPlayer));
                break;
            case "FORCE_UNREGISTER":
                proxy.getEventManager().fireAndForget(new ProxyForcedUnregisterEvent(loggedPlayer));
                break;
            default: break;
        }
        continuation.resume();
    }

    private void createServerConnectionRequest(Player loggedPlayer, ServerConnection connection){
        final RegisteredServer loginServer = loggedPlayer.getCurrentServer().orElse(connection).getServer();
        proxy.getEventManager().fireAndForget(new ProxyLoginEvent(loggedPlayer));
        if(config.getToServerOptions().sendToServer()){
            final List<String> serverList = config.getToServerOptions().getTeleportServers();
            final String randomServer = serverList.get(rm.nextInt(serverList.size()));
            proxy.getServer(randomServer).ifPresentOrElse(serverToSend ->
                proxy.getEventManager().fire(new PreSendOnLoginEvent(loggedPlayer, loginServer, serverToSend)).thenAcceptAsync(preSendEvent -> {
                    if(preSendEvent.getResult().isAllowed()){
                        loggedPlayer.createConnectionRequest(serverToSend).connectWithIndication().thenAcceptAsync(result -> {
                            if(!result.booleanValue()) {
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
}
