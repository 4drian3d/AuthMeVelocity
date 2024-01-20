/*
 * Copyright (C) 2024 AuthMeVelocity Contributors
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

package io.github._4drian3d.authmevelocity.velocity.hooks;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.minimessage.tag.Tag;

import static io.github.miniplaceholders.api.utils.Components.FALSE_COMPONENT;
import static io.github.miniplaceholders.api.utils.Components.TRUE_COMPONENT;

public final class AuthMePlaceholders {
    @Inject
    private AuthMeVelocityPlugin plugin;
    @Inject
    private ProxyServer proxyServer;

    public Expansion getExpansion() {
        return Expansion.builder("authme")
                .filter(Player.class)
                // Logged Placeholders
                .audiencePlaceholder("is_logged", (aud, queue, ctx) ->
                        Tag.selfClosingInserting(plugin.isLogged((Player) aud) ? TRUE_COMPONENT : FALSE_COMPONENT))
                .globalPlaceholder("is_player_logged", (queue, ctx) -> {
                    final String playerName = queue.popOr(() -> "you need to provide a player").value();
                    return Tag.selfClosingInserting(
                            proxyServer.getPlayer(playerName)
                                    .map(plugin::isLogged)
                                    .orElse(false) ? TRUE_COMPONENT : FALSE_COMPONENT
                    );
                })
                // In Auth Server placeholders
                .audiencePlaceholder("in_auth_server", (aud, queue, ctx) ->
                        Tag.selfClosingInserting(plugin.isInAuthServer((Player) aud) ? TRUE_COMPONENT : FALSE_COMPONENT))
                .globalPlaceholder("player_in_auth_server", (queue, ctx) -> {
                    String playerName = queue.popOr(() -> "you need to provide a player").value();
                    return Tag.selfClosingInserting(
                            proxyServer.getPlayer(playerName)
                                    .map(plugin::isInAuthServer)
                                    .orElse(false) ? TRUE_COMPONENT : FALSE_COMPONENT
                    );
                })
                .build();
    }
}
