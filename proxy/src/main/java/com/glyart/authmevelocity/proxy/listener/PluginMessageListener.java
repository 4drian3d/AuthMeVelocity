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
import com.velocitypowered.api.event.ResultedEvent.GenericResult;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder.Result;
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
        final boolean cancelled = !(event.getSource() instanceof ServerConnection)
            || !event.getIdentifier().equals(AuthMeVelocityPlugin.AUTHMEVELOCITY_CHANNEL);
        if (cancelled) {
            continuation.resume();
            return;
        }

        ServerConnection connection = ((ServerConnection)event.getSource());

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        final ByteArrayDataInput input = event.dataAsDataStream();
        final String sChannel = input.readUTF();
        final String playername = input.readUTF();
        final @Nullable Player loggedPlayer = proxy.getPlayer(playername).orElse(null);
        switch (sChannel) {
            case "LOGIN" :
                if (loggedPlayer != null && api.addPlayer(loggedPlayer)){
                    proxy.getEventManager().fireAndForget(new ProxyLoginEvent(loggedPlayer));
                    this.createServerConnectionRequest(loggedPlayer, connection);
                }
                break;
            case "LOGOUT":
                if (loggedPlayer != null && api.removePlayer(loggedPlayer)){
                    proxy.getEventManager().fireAndForget(new ProxyLogoutEvent(loggedPlayer));
                }
                break;
            case "REGISTER":
                if (loggedPlayer != null)
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

    private void createServerConnectionRequest(Player player, ServerConnection connection){
        if (!config.getToServerOptions().sendToServer()) {
            return;
        }

        final RegisteredServer loginServer = player.getCurrentServer().orElse(connection).getServer();
        final String randomServer = this.getRandomServer();

        proxy.getServer(randomServer).ifPresentOrElse(server ->
            proxy.getEventManager().fire(new PreSendOnLoginEvent(player, loginServer, server))
                .thenApply(PreSendOnLoginEvent::getResult)
                .thenApply(GenericResult::isAllowed)
                .thenAcceptAsync(allowed -> {
                    if (!allowed) {
                        return;
                    }
                    player.createConnectionRequest(server)
                        .connect()
                        .thenApply(Result::isSuccessful)
                        .thenAcceptAsync(result -> {
                            if(!result) {
                                logger.info("Unable to connect the player {} to the server {}",
                                    player.getUsername(),
                                    server.getServerInfo().getName());
                            }
                    });
                })
        , () -> logger.warn("The server {} does not exist", randomServer));
    }

    private String getRandomServer() {
        final List<String> serverList = config.getToServerOptions().getTeleportServers();
        return serverList.get(rm.nextInt(serverList.size()));
    }
}
