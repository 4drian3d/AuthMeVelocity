package com.glyart.authmevelocity.proxy.config;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

public class AuthMeConfig {
    private static final String HEADER = """
        AuthmeVelocity Proxy

        Original Developer: xQuickGlare
        Current Developer: 4drian3d
        """;
    private static final HoconConfigurationLoader.Builder configBuilder = HoconConfigurationLoader.builder()
        .defaultOptions(opts -> opts
            .shouldCopyDefaults(true)
            .header(HEADER)
        );
    public static void loadConfig(@NotNull Path path, @NotNull Logger logger){
        Path configPath = path.resolve("config.conf");
        final HoconConfigurationLoader loader = configBuilder
            .path(configPath)
            .build();

        try {
            final CommentedConfigurationNode node = loader.load();
            config = node.get(Config.class);
            node.set(Config.class, config);
            loader.save(node);
        } catch (ConfigurateException exception){
            logger.error("Could not load configuration: {}", exception.getMessage());
        }
    }

    @ConfigSerializable
    public static class Config {

        @Comment("List of login/registration servers")
        private Set<String> authservers = Set.of(
            "auth1",
            "auth2"
        );

        private Commands commands = new Commands();

        private ServerOnLogin send = new ServerOnLogin();

        public Set<String> getAuthServers(){
            return this.authservers;
        }

        public Commands getCommandsConfig(){
            return this.commands;
        }

        public ServerOnLogin getToServerOptions(){
            return this.send;
        }
    }
    @ConfigSerializable
    public static class ServerOnLogin {
        @Comment("Send logged in players to another server?")
        private boolean sendToServerOnLogin = false;

        @Comment("""
        List of servers to send
        One of these servers will be chosen at random
        """)
        private List<String> teleportServers = List.of(
            "lobby1",
            "lobby2"
        );

        public boolean sendToServer(){
            return this.sendToServerOnLogin;
        }

        public List<String> getTeleportServers(){
            return this.teleportServers;
        }
    }

    @ConfigSerializable
    public static class Commands{
        @Comment("Sets the commands that users who have not yet logged in can execute")
        private Set<String> allowedCommands = Set.of(
            "login",
            "register",
            "l",
            "reg",
            "email",
            "captcha"
        );

        @Comment("""
        Sets the message to send in case a non-logged-in player executes an unauthorized command
        To deactivate the message, leave it empty""")
        private String blockedCommandMessage = "&4You cannot execute commands if you are not logged in yet.";

        public Set<String> getAllowedCommands(){
            return this.allowedCommands;
        }

        public String getBlockedMessage() {
            return this.blockedCommandMessage;
        }
    }
    private static Config config;
    public static Config getConfig(){
        return config;
    }
    private AuthMeConfig(){}
}
