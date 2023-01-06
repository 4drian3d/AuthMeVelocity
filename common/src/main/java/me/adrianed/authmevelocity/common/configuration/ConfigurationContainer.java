package me.adrianed.authmevelocity.common.configuration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public class ConfigurationContainer<C> {
    private C config;
    private final HoconConfigurationLoader loader;
    private final Class<C> clazz;

    public ConfigurationContainer(
        final C config,
        final Class<C> clazz,
        final HoconConfigurationLoader loader
    ) {
        this.config = config;
        this.loader = loader;
        this.clazz = clazz;
    }

    public C get() {
        return this.config;
    }

    public CompletableFuture<Void> reload() {
        return CompletableFuture.runAsync(() -> {
            C newConfig = null;
            try {
                final CommentedConfigurationNode node = loader.load();
                newConfig = node.get(clazz);
            } catch (ConfigurateException exception) {
                throw new CompletionException("Could not load config.conf file", exception);
            } finally {
                if (newConfig != null) {
                    config = newConfig;
                }
            }
        });
    }
}
