package com.glyart.authmevelocity.proxy.config;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.moandjiezana.toml.Toml;

import org.jetbrains.annotations.NotNull;

public final class AuthMeConfig {
    private final List<String> authServers;
    private final ServerOnLogin serverOnLogin;
    private final Commands commands;
    private final EnsureAuthServer ensure;

    public AuthMeConfig(@NotNull Toml toml){
        this.authServers = Objects.requireNonNull(toml.getList("authServers"), "the list of auth servers is not available, please check your configuration for any failure");
        this.serverOnLogin = Objects.requireNonNull(toml.getTable("SendOnLogin"), "SendOnLogin options are not available, check your configuration").to(ServerOnLogin.class);
        this.commands = Objects.requireNonNull(toml.getTable("Commands"), "Commands options are not available, check your configuration").to(Commands.class);
        this.ensure = Objects.requireNonNull(toml.getTable("EnsureAuthServer"), "EnsureAuthServer options are not available, check your configuration").to(EnsureAuthServer.class);
    }

    public static class ServerOnLogin {
        private boolean sendToServerOnLogin;
        private List<String> teleportServers;

        public boolean sendToServer(){
            return this.sendToServerOnLogin;
        }

        public @NotNull List<String> getTeleportServers(){
            return this.teleportServers;
        }
    }

    public static class Commands{
        private Set<String> allowedCommands;
        private String blockedCommandMessage;

        public @NotNull Set<String> getAllowedCommands(){
            return this.allowedCommands;
        }

        public @NotNull String getBlockedMessage() {
            return this.blockedCommandMessage;
        }
    }

    public static class EnsureAuthServer {
        private boolean ensureFirstServerIsAuthServer;
        private String disconnectMessage;

        public boolean ensureAuthServer(){
            return this.ensureFirstServerIsAuthServer;
        }

        public @NotNull String getDisconnectMessage(){
            return this.disconnectMessage;
        }

    }

    public @NotNull Commands getCommandsConfig(){
        return this.commands;
    }

    public @NotNull ServerOnLogin getToServerOptions(){
        return this.serverOnLogin;
    }

    public @NotNull EnsureAuthServer getEnsureOptions(){
        return this.ensure;
    }

    public @NotNull List<String> getAuthServers(){
        return this.authServers;
    }
}
