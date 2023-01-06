package me.adrianed.authmevelocity.common.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public final class Loader {
    private Loader() {}
    public static <C> ConfigurationContainer<C> loadMainConfig(Path path, Class<C> clazz) throws IOException {
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


        final CommentedConfigurationNode node = loader.load();
        final C config = node.get(clazz);
        if (firstCreation) {
            node.set(clazz, config);
            loader.save(node);
        }

        return new ConfigurationContainer<>(config, clazz, loader);
    }
}
