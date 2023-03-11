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

package io.github._4drian3d.authmevelocity.common;

import io.github._4drian3d.authmevelocity.common.configuration.ConfigurationContainer;
import io.github._4drian3d.authmevelocity.common.configuration.PaperConfiguration;
import io.github._4drian3d.authmevelocity.common.configuration.ProxyConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationTest {
    @TempDir
    Path path;

    @Test
    void testProxyConfigurationCreation() {
        final ConfigurationContainer<ProxyConfiguration> proxyConfiguration =
                assertDoesNotThrow(
                    () -> ConfigurationContainer.load(path, ProxyConfiguration.class)
                );

        assertThat(proxyConfiguration)
                .isNotNull()
                .extracting(ConfigurationContainer::get)
                .isNotNull();
    }

    @Test
    void testPaperConfigurationCreation() {
        final ConfigurationContainer<PaperConfiguration> proxyConfiguration =
                assertDoesNotThrow(
                        () -> ConfigurationContainer.load(path, PaperConfiguration.class)
                );

        assertThat(proxyConfiguration)
                .isNotNull()
                .extracting(ConfigurationContainer::get)
                .isNotNull()
                .extracting(PaperConfiguration::debug)
                .isEqualTo(false);

    }
}
