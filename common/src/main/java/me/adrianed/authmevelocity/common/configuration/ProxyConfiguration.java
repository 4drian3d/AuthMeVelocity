package me.adrianed.authmevelocity.common.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import me.adrianed.authmevelocity.common.enums.SendMode;

import java.util.List;

@ConfigSerializable
public class ProxyConfiguration {
    @Comment("Enable debug mode")
    private boolean debug = false;
    public boolean debug() {
        return this.debug;
    }

    @Comment("")
    private int randomAttempts = 5;
    public int randomAttempts() {
        return this.randomAttempts;
    }

    @Comment("List of login/registration servers")
    private List<String> authServers = List.of("auth1", "auth2");
    public List<String> authServers() {
        return this.authServers;
    }
    
    private SendOnLogin sendOnLogin = new SendOnLogin();
    public SendOnLogin sendOnLogin() {
        return this.sendOnLogin;
    }

    private Commands commands = new Commands();
    public Commands commands() {
        return this.commands;
    }

    private EnsureAuthServer ensureAuthServer = new EnsureAuthServer();
    public EnsureAuthServer ensureAuthServer() {
        return this.ensureAuthServer;
    }

    @ConfigSerializable
    public static class EnsureAuthServer {
        @Comment("Ensure that the first server to which players connect is an auth server")
        private boolean ensureAuthServer = false;
        public boolean ensureFirstServerIsAuthServer() {
            return this.ensureAuthServer;
        }

        @Comment("""
                SendMode 
                TO_FIRST | 
                TO_EMPTIEST_SERVE | 
                RANDOM |
                """)
        private SendMode sendMode = SendMode.RANDOM;
        public SendMode sendMode() {
            return this.sendMode;
        }
    }

    @ConfigSerializable
    public static class SendOnLogin {
        @Comment("Send logged in players to another server?")
        private boolean sendOnLogin = false;
        public boolean sendToServerOnLogin() {
            return this.sendOnLogin;
        }

        @Comment("""
            List of servers to send
            One of these servers will be chosen at random
                """)
        private List<String> teleportServers = List.of("lobby1", "lobby2");
        public List<String> teleportServers() {
            return this.teleportServers;
        }

        // TODO: Improve comments
        @Comment("""
            SendMode 
            TO_FIRST | 
            TO_EMPTIEST_SERVE | 
            RANDOM |
            """)
        private SendMode sendMode = SendMode.RANDOM;
        public SendMode sendMode() {
            return this.sendMode;
        }
    }

    @ConfigSerializable
    public static class Commands {
        @Comment("Sets the commands that users who have not yet logged in can execute")
        private List<String> allowedCommands = List.of("login", "register", "l", "reg", "email", "captcha");
        public List<String> allowedCommands() {
            return this.allowedCommands;
        }

        @Comment("""
            Sets the message to send in case a non-logged-in player executes an unauthorized command
            To deactivate the message, leave it empty
                """)
        private String blockedMessage = "<red>You cannot execute commands if you are not logged in yet";
        public String blockedCommandMessage() {
            return this.blockedMessage;
        }
    }

    
    
}
