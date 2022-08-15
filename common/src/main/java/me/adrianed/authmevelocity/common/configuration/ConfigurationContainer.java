package me.adrianed.authmevelocity.common.configuration;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public class ConfigurationContainer<C> {
    private C config;
    private final HoconConfigurationLoader loader;
    private final Class<C> clazz;
    private final Logger logger;

    public ConfigurationContainer(
        final C config,
        final Class<C> clazz,
        final HoconConfigurationLoader loader,
        final Logger logger
    ) {
        this.config = config;
        this.loader = loader;
        this.clazz = clazz;
        this.logger = logger;
    }

    public CompletableFuture<Boolean> reload() {
        return this.safeReload();
    }

    public void setValues(Consumer<C> consumer) {
        consumer.accept(this.config);
        this.safeReload();
    }

    public C get() {
        return this.config;
    }

    private final CompletableFuture<Boolean> safeReload() {
        return CompletableFuture.supplyAsync(() -> {
            C newConfig = null;
            try {
                final CommentedConfigurationNode node = loader.load();
                newConfig = node.get(clazz);
                return true;
            } catch (ConfigurateException exception) {
                logger.error("Could not load config.conf file", exception);
                return false;
            } finally {
                if (newConfig != null) {
                    config = newConfig;
                }
            }
        });
    }
}
