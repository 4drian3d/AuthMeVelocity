package com.glyart.authmevelocity.proxy.listener;

import com.glyart.authmevelocity.proxy.AuthMeVelocityPlugin;
import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Optional;
import java.util.UUID;

public class ProxyListener {

    private final AuthMeVelocityPlugin plugin;
    private final ProxyServer server;

    public ProxyListener(AuthMeVelocityPlugin plugin) {
        this.plugin = plugin;
        server = plugin.server;
    }

    @Subscribe
    public void onPluginMessage(final PluginMessageEvent event) {
        if (!(event.getSource() instanceof ServerConnection)) {
            return;
        }

        if (!event.getIdentifier().getId().equals("authmevelocity:main")) {
            return;
        }

        ByteArrayDataInput input = event.dataAsDataStream();
        String sChannel = input.readUTF();
        if (!sChannel.equals("LOGIN")) {
            return;
        }

        String user = input.readUTF();
        Optional<Player> player = server.getPlayer(UUID.fromString(user));
        if (!player.isPresent()) {
            return;
        }

        plugin.loggedPlayers.add(player.get().getUniqueId());
    }

    @Subscribe
    public void onDisconnect(final DisconnectEvent event) {
        plugin.loggedPlayers.remove(event.getPlayer().getUniqueId());
    }

    @Subscribe
    public void onCommandExecute(final CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player))
            return;

        final var player = (Player) event.getCommandSource();
        if (plugin.loggedPlayers.contains(player.getUniqueId()))
            return;

        Optional<ServerConnection> server = player.getCurrentServer();
        boolean isAuthServer =
            server.isPresent() &&
            AuthMeVelocityPlugin.getConfig().getList("authservers").contains(server.get().getServerInfo().getName());

        if (isAuthServer) {
            event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
        }
        else {
            event.setResult(CommandExecuteEvent.CommandResult.denied());
        }
    }

    @Subscribe
    public void onPlayerChat(final PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.loggedPlayers.contains(player.getUniqueId()))
            return;

        Optional<ServerConnection> server = player.getCurrentServer();
        if (server.isPresent() && AuthMeVelocityPlugin.getConfig().getList("authservers").contains(server.get().getServerInfo().getName())) {
            return;
        }

        event.setResult(PlayerChatEvent.ChatResult.denied());
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        if (plugin.loggedPlayers.contains(player.getUniqueId()))
            return;

        Optional<RegisteredServer> server = event.getResult().getServer();
        if (server.isPresent() && AuthMeVelocityPlugin.getConfig().getList("authservers").contains(server.get().getServerInfo().getName())) {
            return;
        }

        event.setResult(ServerPreConnectEvent.ServerResult.denied());
    }
}
