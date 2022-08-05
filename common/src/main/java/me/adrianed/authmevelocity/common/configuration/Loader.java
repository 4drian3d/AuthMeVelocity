package me.adrianed.authmevelocity.common.configuration;

import java.nio.file.Path;

import org.slf4j.Logger;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;


public class Loader {
    public static <C> ConfigurationContainer<C> loadMainConfig(final Path path, Class<C> clazz, Logger logger){
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
            .defaultOptions(opts -> opts
                .shouldCopyDefaults(true)
                .header("AuthMeVelocity | by Glyart	& 4drian3d\n")
            )
            .path(path.resolve("config.conf"))
            .build();

        final C config;
        try {
            final CommentedConfigurationNode node = loader.load();
            config = node.get(clazz);
            node.set(clazz, config);
            loader.save(node);
        } catch (ConfigurateException exception){
            logger.error("Could not load config.conf file", exception);
            return null;
        }
        return new ConfigurationContainer<>(config, clazz, loader, logger);
    }
}
