package com.glyart.authmevelocity.proxy.config;

import java.io.File;
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
        Actual Developer: 4drian3d
        """;
    private static final HoconConfigurationLoader.Builder configBuilder = HoconConfigurationLoader.builder()
        .defaultOptions(opts -> opts
            .shouldCopyDefaults(true)
            .header(HEADER)
        );
    public static void loadConfig(@NotNull Path path, @NotNull Logger logger){
        File configFile = new File(path.toFile(), "config.conf");
        final HoconConfigurationLoader loader = configBuilder
            .file(configFile)
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

        private ServerOnLogin send = new ServerOnLogin();

        public Set<String> getAuthServers(){
            return this.authservers;
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
    private static Config config;
    public static Config getConfig(){
        return config;
    }
    private AuthMeConfig(){}
}
