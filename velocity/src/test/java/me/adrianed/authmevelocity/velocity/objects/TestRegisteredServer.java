package me.adrianed.authmevelocity.velocity.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.proxy.server.ServerPing.Version;

import net.kyori.adventure.text.Component;

public class TestRegisteredServer implements RegisteredServer {
    private List<Player> players = new ArrayList<>();
    private final ServerInfo info;

    public TestRegisteredServer(ServerInfo info) {
        this.info = info;
    }
    
    @Override
    public boolean sendPluginMessage(ChannelIdentifier identifier, byte[] data) {
        return true;
    }

    @Override
    public ServerInfo getServerInfo() {
        return this.info;
    }

    @Override
    public Collection<Player> getPlayersConnected() {
        return players;
    }

    @Override
    public CompletableFuture<ServerPing> ping() {
        return CompletableFuture.completedFuture(
            new ServerPing(
                new Version(404, "nose"),
                new ServerPing.Players(0, 4, Collections.emptyList()),
                Component.empty(), null));
    }
    
}
