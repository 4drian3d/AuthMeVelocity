package com.glyart.authmevelocity.proxy.config;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class AuthMeConfig {
    public static void loadConfig(@NotNull Path path, @NotNull Logger logger){
        File configFile = new File(path.toFile(), "config.yml");
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
            .defaultOptions(opts -> opts.shouldCopyDefaults(true))
            .file(configFile)
            .build();

        try {
            CommentedConfigurationNode node = loader.load();
            config = node.get(Config.class);
            node.set(Config.class, config);
            loader.save(node);
        } catch (ConfigurateException exception){
            logger.error("Could not load configuration: {}", exception.getMessage());
        }
    }
    @ConfigSerializable
    public static class Config {

        @Comment("List of authservers")
        private Set<String> authservers = Set.of(
            "auth1",
            "auth2"
        );

        @Comment("Send each player to another server on login?")
        private boolean sendToServerOnLogin = false;

        @Comment("List of teleport to servers")
        private List<String> teleportServers = List.of(
            "lobby1",
            "lobby2"
        );

        public Set<String> getAuthServers(){
            return authservers;
        }

        public boolean sendToServer(){
            return sendToServerOnLogin;
        }

        public List<String> getTeleportServers(){
            return teleportServers;
        }
    }
    private static Config config;
    public static Config getConfig(){
        return config;
    }
    private AuthMeConfig(){}
}
