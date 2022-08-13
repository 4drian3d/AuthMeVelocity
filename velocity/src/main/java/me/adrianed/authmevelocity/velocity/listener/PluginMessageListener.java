package me.adrianed.authmevelocity.velocity.listener;

import java.util.Locale;

import me.adrianed.authmevelocity.api.velocity.event.PreSendOnLoginEvent;
import me.adrianed.authmevelocity.api.velocity.event.ProxyForcedUnregisterEvent;
import me.adrianed.authmevelocity.api.velocity.event.ProxyLoginEvent;
import me.adrianed.authmevelocity.api.velocity.event.ProxyLogoutEvent;
import me.adrianed.authmevelocity.api.velocity.event.ProxyRegisterEvent;
import me.adrianed.authmevelocity.api.velocity.event.ProxyUnregisterEvent;
import me.adrianed.authmevelocity.common.MessageType;
import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.adrianed.authmevelocity.velocity.AuthMeVelocityPlugin;
import me.adrianed.authmevelocity.velocity.utils.AuthmeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class PluginMessageListener {
    private final ProxyServer proxy;
    private final Logger logger;
    private final AuthMeVelocityPlugin plugin;

    public PluginMessageListener(@NotNull ProxyServer proxy, @NotNull Logger logger, AuthMeVelocityPlugin plugin) {
        this.proxy = proxy;
        this.logger = logger;
        this.plugin = plugin;
    }

    @Subscribe
    public void onPluginMessage(final PluginMessageEvent event, Continuation continuation) {
        final boolean cancelled = !event.getResult().isAllowed()
            || !(event.getSource() instanceof ServerConnection)
            || !event.getIdentifier().equals(AuthMeVelocityPlugin.AUTHMEVELOCITY_CHANNEL);
        if (cancelled) {
            continuation.resume();
            plugin.logDebug("PluginMessageEvent | Not allowed");
            return;
        }

        final ServerConnection connection = (ServerConnection) event.getSource();

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        final ByteArrayDataInput input = event.dataAsDataStream();
        final String message = input.readUTF();
        final MessageType type = MessageType.INDEX.value(
            message.toUpperCase(Locale.ROOT));
        final String name = input.readUTF();
        final @Nullable Player player = proxy.getPlayer(name).orElse(null);

        switch (type) {
            case LOGIN -> {
                plugin.logDebug("PluginMessageEvent | Login type");
                if (player != null && plugin.addPlayer(player)) {
                    proxy.getEventManager().fireAndForget(new ProxyLoginEvent(player));
                    this.createServerConnectionRequest(player, connection);
                    plugin.logDebug("PluginMessageEvent | Player not null");
                }
            }
            case LOGOUT -> {
                plugin.logDebug("PluginMessageEvent | Logout type");
                if (player != null && plugin.removePlayer(player)){
                    proxy.getEventManager().fireAndForget(new ProxyLogoutEvent(player));
                    plugin.logDebug("PluginMessageEvent | Player not null");
                }
            }
            case REGISTER -> {
                plugin.logDebug("PluginMessageEvent | Register");
                if (player != null) {
                    proxy.getEventManager().fireAndForget(new ProxyRegisterEvent(player));
                    plugin.logDebug("PluginMessageEvent | Player not null");
                }
            }
            case UNREGISTER -> {
                plugin.logDebug("PluginMessageEvent | Unregister type");
                if (player != null) {
                    plugin.logDebug("PluginMessageEvent | Player not null");
                    proxy.getEventManager().fireAndForget(new ProxyUnregisterEvent(player));
                } 
            }
            case FORCE_UNREGISTER -> {
                proxy.getEventManager().fireAndForget(new ProxyForcedUnregisterEvent(player));
                plugin.logDebug("PluginMessageEvent | Forced Unregister type");
            }
                
        }
        continuation.resume();
    }

    private void createServerConnectionRequest(Player player, ServerConnection connection){
        if (!plugin.config().get().sendOnLogin().sendToServerOnLogin()) {
            return;
        }

        final RegisteredServer loginServer = player.getCurrentServer().orElse(connection).getServer();

        var config = plugin.config().get();

        var toSend = AuthmeUtils.serverToSend(
            config.sendOnLogin().sendMode(), proxy, config.authServers(), config.advanced().randomAttempts());

        if (toSend.isEmpty()) {
            if (toSend.string() != null) {
                logger.warn("The server {} does not exist", toSend.string());
            } else {
                logger.warn("There is not valid server to send");
            }
            return;
        }

        proxy.getEventManager().fire(new PreSendOnLoginEvent(player, loginServer, toSend.object()))
                .thenAccept(event -> {
                    if (!event.getResult().isAllowed()) {
                        return;
                    }
                    player.createConnectionRequest(event.getResult().server())
                        .connect()
                        .thenAcceptAsync(result -> {
                            if (!result.isSuccessful()) {
                                logger.info("Unable to connect the player {} to the server {}",
                                    player.getUsername(),
                                    result.getAttemptedConnection().getServerInfo().getName());
                            }
                    });
                });
    }
}
