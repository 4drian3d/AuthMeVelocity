package com.glyart.authmevelocity.proxy.listener;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.glyart.authmevelocity.proxy.AuthMeVelocityPlugin;
import com.glyart.authmevelocity.proxy.AuthmeVelocityAPI;
import com.glyart.authmevelocity.proxy.config.AuthMeConfig;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public class ConnectListener {
    private final ProxyServer proxy;
    private final Logger logger;
    private final AuthMeConfig config;
    private final AuthmeVelocityAPI api;
    
    public ConnectListener(AuthMeConfig config, AuthmeVelocityAPI api, ProxyServer proxy, Logger logger) {
        this.config = config;
        this.api = api;
        this.logger = logger;
        this.proxy = proxy;
    }

    @Subscribe(order = PostOrder.LATE)
    public void onInitialServer(PlayerChooseInitialServerEvent event, Continuation continuation){
        if(!config.getEnsureOptions().ensureAuthServer()) {
            continuation.resume();
            return;
        }

        Optional<RegisteredServer> optionalSV = event.getInitialServer();
        if(optionalSV.isPresent() && api.isAuthServer(optionalSV.get())){
            continuation.resume();
            return;
        }

        @Nullable RegisteredServer server = getAvailableServer();
        // Velocity takes over in case the initial server is not present
        event.setInitialServer(server);
        continuation.resume();
        if (server == null) {
            logger.error("Cannot send the player {} to an auth server", event.getPlayer().getUsername());
        }
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event, Continuation continuation) {
        if (!event.getResult().isAllowed() || api.isLogged(event.getPlayer())) {
            continuation.resume();
            return;
        }

        // this should be present, "event.getResult().isAllowed()" is the "isPresent" check
        if(!api.isAuthServer(event.getResult().getServer().get())) {
            event.setResult(ServerPreConnectEvent.ServerResult.denied());
        }
        continuation.resume();
    }

    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        final Player player = event.getPlayer();
        if (api.isLogged(player) && api.isInAuthServer(player)) {
            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            buf.writeUTF("LOGIN");
            player.getCurrentServer().ifPresent(sv ->
                sv.sendPluginMessage(AuthMeVelocityPlugin.AUTHMEVELOCITY_CHANNEL, buf.toByteArray()));
        }
    }

    // TODO: Implement #40
    private @Nullable RegisteredServer getAvailableServer() {
        for(String sv : config.getAuthServers()){
            Optional<RegisteredServer> opt = proxy.getServer(sv);
            if (opt.isPresent()) return opt.get();
        }
        return null;
    }
}
