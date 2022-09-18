package me.adrianed.authmevelocity.velocity.objects;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.config.ProxyConfig;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.ChannelRegistrar;
import com.velocitypowered.api.proxy.player.ResourcePackInfo.Builder;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.scheduler.Scheduler;
import com.velocitypowered.api.util.ProxyVersion;

import net.kyori.adventure.text.Component;

public class TestProxyServer implements ProxyServer, ConsoleCommandSource {
    private final List<Player> players = new ArrayList<>();
    private final List<RegisteredServer> servers = new ArrayList<>();
    private CommandManager manager;

    public TestProxyServer(CommandManager manager) {
        this.manager = manager;
    }
    @Override
    public void shutdown(Component reason) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public Optional<Player> getPlayer(String username) {
        return players.stream()
            .filter(player -> player.getUsername().equals(username))
            .findAny();
    }

    @Override
    public Optional<Player> getPlayer(UUID uuid) {
        return players.stream()
            .filter(player -> player.getUniqueId().equals(uuid))
            .findAny();
    }

    @Override
    public Collection<Player> getAllPlayers() {
        return players;
    }

    @Override
    public int getPlayerCount() {
        return 0;
    }

    @Override
    public Optional<RegisteredServer> getServer(String name) {
        return servers.stream()
            .filter(server -> server.getServerInfo().getName().equals(name))
            .findAny();
    }

    @Override
    public Collection<RegisteredServer> getAllServers() {
        return servers;
    }

    @Override
    public Collection<Player> matchPlayer(String partialName) {
        return players.stream()
            .filter(player -> player.getUsername().startsWith(partialName))
            .toList();
    }

    @Override
    public Collection<RegisteredServer> matchServer(String partialName) {
        return servers.stream()
            .filter(server -> server.getServerInfo().getName().startsWith(partialName))
            .toList();
    }

    @Override
    public RegisteredServer createRawRegisteredServer(ServerInfo server) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RegisteredServer registerServer(ServerInfo server) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void unregisterServer(ServerInfo server) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ConsoleCommandSource getConsoleCommandSource() {
        return this;
    }

    @Override
    public PluginManager getPluginManager() {
        return null;
    }

    @Override
    public EventManager getEventManager() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CommandManager getCommandManager() {
        return manager;
    }

    @Override
    public Scheduler getScheduler() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChannelRegistrar getChannelRegistrar() {
        return new ChannelRegistrar() {

            @Override
            public void register(ChannelIdentifier... identifiers) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void unregister(ChannelIdentifier... identifiers) {
                // TODO Auto-generated method stub
                
            }
            
        };
    }

    @Override
    public InetSocketAddress getBoundAddress() {
        // TODO Auto-generated method stub
        return new InetSocketAddress("play.peruviankkit.net", 25565);
    }

    @Override
    public ProxyConfig getConfiguration() {
        return null;
    }

    @Override
    public ProxyVersion getVersion() {
        return new ProxyVersion("Velovity", "4drian3d", "3.2.0");
    }

    @Override
    public Builder createResourcePackBuilder(String url) {
        return null;
    }

    @Override
    public Tristate getPermissionValue(String permission) {
        return Tristate.TRUE;
    }
    
}
