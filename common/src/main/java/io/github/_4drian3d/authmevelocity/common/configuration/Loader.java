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

package io.github._4drian3d.authmevelocity.common.configuration;

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
