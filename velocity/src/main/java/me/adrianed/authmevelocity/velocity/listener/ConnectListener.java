package me.adrianed.authmevelocity.velocity.listener;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

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

import me.adrianed.authmevelocity.velocity.AuthMeVelocityPlugin;
import me.adrianed.authmevelocity.api.velocity.AuthMeVelocityAPI;

public class ConnectListener {
    private final ProxyServer proxy;
    private final Logger logger;
    private final AuthMeVelocityPlugin plugin;
    
    public ConnectListener(AuthMeVelocityPlugin plugin, ProxyServer proxy, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.proxy = proxy;
    }

    @Subscribe(order = PostOrder.LATE)
    public void onInitialServer(PlayerChooseInitialServerEvent event, Continuation continuation){
        if(!plugin.config().getConfig().ensureAuthServer().ensureFirstServerIsAuthServer()) {
            continuation.resume();
            return;
        }

        Optional<RegisteredServer> optionalSV = event.getInitialServer();
        if(optionalSV.isPresent() && plugin.isAuthServer(optionalSV.get())){
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
        if (!event.getResult().isAllowed() && plugin.isLogged(event.getPlayer())) {
            continuation.resume();
            return;
        }

        // this should be present, "event.getResult().isAllowed()" is the "isPresent" check
        if(!plugin.isAuthServer(event.getResult().getServer().get())) {
            event.setResult(ServerPreConnectEvent.ServerResult.denied());
        }
        continuation.resume();
    }

    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        final Player player = event.getPlayer();
        if (plugin.isLogged(player) && plugin.isInAuthServer(player)) {
            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            buf.writeUTF("LOGIN");
            player.getCurrentServer().ifPresent(sv ->
                sv.sendPluginMessage(AuthMeVelocityPlugin.AUTHMEVELOCITY_CHANNEL, buf.toByteArray()));
        }
    }

    // TODO: Implement #40
    private @Nullable RegisteredServer getAvailableServer() {
        for(String sv : plugin.config().getConfig().authServers()){
            Optional<RegisteredServer> opt = proxy.getServer(sv);
            if (opt.isPresent()) return opt.get();
        }
        return null;
    }
}
