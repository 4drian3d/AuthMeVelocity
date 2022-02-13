package com.glyart.authmevelocity.proxy.config;

import java.util.List;
import java.util.Set;

import com.moandjiezana.toml.Toml;

public final class AuthMeConfig {
    private final List<String> authServers;
    private final ServerOnLogin serverOnLogin;
    private final Commands commands;

    public AuthMeConfig(Toml toml){
        this.authServers = toml.getList("authServers");
        this.serverOnLogin = toml.getTable("SendOnLogin").to(ServerOnLogin.class);
        this.commands = toml.getTable("Commands").to(Commands.class);
    }

    public static class ServerOnLogin {
        private boolean sendToServerOnLogin;
        private List<String> teleportServers;

        public boolean sendToServer(){
            return this.sendToServerOnLogin;
        }

        public List<String> getTeleportServers(){
            return this.teleportServers;
        }
    }

    public static class Commands{
        private Set<String> allowedCommands;
        private String blockedCommandMessage;

        public Set<String> getAllowedCommands(){
            return this.allowedCommands;
        }

        public String getBlockedMessage() {
            return this.blockedCommandMessage;
        }
    }

    public Commands getCommandsConfig(){
        return this.commands;
    }

    public ServerOnLogin getToServerOptions(){
        return this.serverOnLogin;
    }

    public List<String> getAuthServers(){
        return this.authServers;
    }
}
