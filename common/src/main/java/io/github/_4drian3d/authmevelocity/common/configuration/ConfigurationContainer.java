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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public final class ConfigurationContainer<C> {
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
            try {
                final CommentedConfigurationNode node = loader.load();
                config = node.get(clazz);
            } catch (ConfigurateException exception) {
                throw new CompletionException("Could not load config.conf file", exception);
            }
        });
    }
}
