/*
 * Copyright (C) 2023 AuthMeVelocity Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.adrianed.authmevelocity.common.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import me.adrianed.authmevelocity.common.enums.SendMode;

import java.util.List;

@ConfigSerializable
public class ProxyConfiguration {
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

    private Advanced advanced = new Advanced();
    public Advanced advanced() {
        return this.advanced;
    }

    @ConfigSerializable
    public static class EnsureAuthServer {
        @Comment("Ensure that the first server to which players connect is an auth server")
        private boolean ensureAuthServer = false;
        public boolean ensureFirstServerIsAuthServer() {
            return this.ensureAuthServer;
        }

        @Comment("""
            Selection Mode of the player's initial server
            TO_FIRST | Send to the first valid server configured
            TO_EMPTIEST_SERVER | Send to the server with the lowest number of players
            RANDOM | Send to a random server""")
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
            One of these servers will be chosen at random""")
        private List<String> teleportServers = List.of("lobby1", "lobby2");
        public List<String> teleportServers() {
            return this.teleportServers;
        }

        @Comment("""
            Selection Mode of the server to which the player will be sent
            TO_FIRST | Send to the first valid server configured
            TO_EMPTIEST_SERVER | Send to the server with the lowest number of players
            RANDOM | Send to a random server""")
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
            To deactivate the message, leave it empty""")
        private String blockedMessage = "<red>You cannot execute commands if you are not logged in yet";
        public String blockedCommandMessage() {
            return this.blockedMessage;
        }
    }

    @ConfigSerializable
    public static class Advanced {
        @Comment("Enable debug mode")
        private boolean debug = false;
        public boolean debug() {
            return this.debug;
        }
    
        @Comment("Attempts to get a valid server in SendMode Random")
        private int randomAttempts = 5;
        public int randomAttempts() {
            return this.randomAttempts;
        }

        @Comment("""
            Ignore blocking of commands and chat messages to 1.19.1 clients with a valid signed key
            When trying to block these executions, the proxy will kick the player out.
            This option allows you to prevent the plugin from trying to block these executions,
            avoiding the player to be kicked out""")
        private boolean ignoreSignedPlayers = false;
        public boolean ignoreSignedPlayers() {
            return this.ignoreSignedPlayers;
        }
    }

    
    
}
