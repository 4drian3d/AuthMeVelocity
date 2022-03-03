package com.glyart.authmevelocity.proxy.config;

import java.util.List;
import java.util.Set;

import com.moandjiezana.toml.Toml;

import org.jetbrains.annotations.NotNull;

public final class AuthMeConfig {
    private final List<String> authServers;
    private final ServerOnLogin serverOnLogin;
    private final Commands commands;
    private final EnsureAuthServer ensure;

    public AuthMeConfig(@NotNull Toml toml){
        this.authServers = toml.getList("authServers", List.of("auth1", "auth2"));
        this.serverOnLogin = ConfigUtils.getOrElse(toml, "SendOnLogin", new ServerOnLogin(false, List.of("lobby1", "lobby2")));
        this.commands = ConfigUtils.getOrElse(toml, "Commands", new Commands(Set.of("login", "register", "l", "reg", "email", "captcha"),"<red>You cannot execute commands if you are not logged in yet"));
        this.ensure = ConfigUtils.getOrElse(toml, "EnsureAuthServer", new EnsureAuthServer(false, "<red>You could not connect to a login server, please try again later"));
    }

    public static class ServerOnLogin {
        private boolean sendToServerOnLogin;
        private List<String> teleportServers;

        public ServerOnLogin(boolean sendToServerOnLogin, List<String> teleportServers){
            this.sendToServerOnLogin = sendToServerOnLogin;
            this.teleportServers = teleportServers;
        }

        public boolean sendToServer(){
            return this.sendToServerOnLogin;
        }

        public @NotNull List<String> getTeleportServers(){
            return this.teleportServers;
        }
    }

    public static class Commands {
        private Set<String> allowedCommands;
        private String blockedCommandMessage;

        public Commands(Set<String> allowedCommands, String blockedCommandMessage){
            this.allowedCommands = allowedCommands;
            this.blockedCommandMessage = blockedCommandMessage;
        }

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

        public EnsureAuthServer(boolean ensureFirstServerIsAuthServer, String disconnectMessage){
            this.ensureFirstServerIsAuthServer = ensureFirstServerIsAuthServer;
            this.disconnectMessage = disconnectMessage;
        }

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
