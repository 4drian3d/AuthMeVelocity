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

package io.github._4drian3d.authmevelocity.velocity;

import com.velocitypowered.api.proxy.Player;
import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.minimessage.tag.Tag;

import static io.github.miniplaceholders.api.utils.Components.FALSE_COMPONENT;
import static io.github.miniplaceholders.api.utils.Components.TRUE_COMPONENT;

final class AuthMePlaceholders {
    private AuthMePlaceholders() {
    }

    static Expansion getExpansion(AuthMeVelocityPlugin plugin) {
        return Expansion.builder("authme")
                .filter(Player.class)
                // Logged Placeholders
                .audiencePlaceholder("is_logged", (aud, queue, ctx) ->
                        Tag.selfClosingInserting(plugin.isLogged((Player) aud) ? TRUE_COMPONENT : FALSE_COMPONENT))
                .globalPlaceholder("is_player_logged", (queue, ctx) -> {
                    String playerName = queue.popOr(() -> "you need to provide a player").value();
                    return Tag.selfClosingInserting(
                            plugin.getProxy().getPlayer(playerName)
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
                            plugin.getProxy().getPlayer(playerName)
                                    .map(plugin::isInAuthServer)
                                    .orElse(false) ? TRUE_COMPONENT : FALSE_COMPONENT
                    );
                })
                .build();
    }
}
