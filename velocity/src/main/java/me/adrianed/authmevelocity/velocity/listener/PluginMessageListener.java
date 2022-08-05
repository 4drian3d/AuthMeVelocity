package me.adrianed.authmevelocity.velocity.listener;

import java.util.List;
import java.util.Random;

import me.adrianed.authmevelocity.api.velocity.event.PreSendOnLoginEvent;
import me.adrianed.authmevelocity.api.velocity.event.ProxyForcedUnregisterEvent;
import me.adrianed.authmevelocity.api.velocity.event.ProxyLoginEvent;
import me.adrianed.authmevelocity.api.velocity.event.ProxyLogoutEvent;
import me.adrianed.authmevelocity.api.velocity.event.ProxyRegisterEvent;
import me.adrianed.authmevelocity.api.velocity.event.ProxyUnregisterEvent;
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

import me.adrianed.authmevelocity.velocity.AuthMeVelocityPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class PluginMessageListener {
    private final ProxyServer proxy;
    private final Logger logger;
    private final Random rm;
    private final AuthMeVelocityPlugin plugin;

    public PluginMessageListener(@NotNull ProxyServer proxy, @NotNull Logger logger, AuthMeVelocityPlugin plugin) {
        this.proxy = proxy;
        this.logger = logger;
        this.rm = new Random();
        this.plugin = plugin;
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
                if (loggedPlayer != null && plugin.addPlayer(loggedPlayer)){
                    proxy.getEventManager().fireAndForget(new ProxyLoginEvent(loggedPlayer));
                    this.createServerConnectionRequest(loggedPlayer, connection);
                }
                break;
            case "LOGOUT":
                if (loggedPlayer != null && plugin.removePlayer(loggedPlayer)){
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
        if (!plugin.config().getConfig().sendOnLogin().sendToServerOnLogin()) {
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
        final List<String> serverList = plugin.config().getConfig().sendOnLogin().teleportServers();
        return serverList.get(rm.nextInt(serverList.size()));
    }
}
