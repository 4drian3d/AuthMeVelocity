package me.adrianed.authmevelocity.common.configuration;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public final class Loader {
    private Loader() {}
    public static <C> ConfigurationContainer<C> loadMainConfig(Path path, Class<C> clazz, Logger logger) {
        path = path.resolve("config.conf");
        final boolean firstCreation = Files.notExists(path);
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
            .defaultOptions(opts -> opts
                .shouldCopyDefaults(true)
                .header("""
                    AuthMeVelocity | by Glyart & 4drian3d
                    """)
            )
            .path(path)
            .build();

        final C config;
        try {
            final CommentedConfigurationNode node = loader.load();
            config = node.get(clazz);
            if (firstCreation) {
                node.set(clazz, config);
                loader.save(node);
            }
            
        } catch (ConfigurateException exception){
            logger.error("Could not load config.conf file", exception);
            return null;
        }
        return new ConfigurationContainer<>(config, clazz, loader, logger);
    }
}
