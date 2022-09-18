package me.adrianed.authmevelocity.velocity.objects;

import java.util.Optional;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

public class TestServerConnection implements ServerConnection {
    private final RegisteredServer server;
    private final Player player;
    public TestServerConnection(RegisteredServer server, Player player) {
        this.server = server;
        this.player = player;
    }

    @Override
    public boolean sendPluginMessage(ChannelIdentifier identifier, byte[] data) {
        return true;
    }

    @Override
    public RegisteredServer getServer() {
        return server;
    }

    @Override
    public Optional<RegisteredServer> getPreviousServer() {
        return Optional.empty();
    }

    @Override
    public ServerInfo getServerInfo() {
        return server.getServerInfo();
    }

    @Override
    public Player getPlayer() {
        return player;
    }
    
}
