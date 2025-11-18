/*
 * Copyright (C) 2025 AuthMeVelocity Contributors
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

package io.github._4drian3d.authmevelocity.paper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.xephi.authme.api.v3.AuthMeApi;
import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.minimessage.tag.Tag;

import static io.github.miniplaceholders.api.utils.Components.*;

final class AuthMePlaceholders {
    private AuthMePlaceholders() {}

    static Expansion getExpansion() {
        return Expansion.builder("authme")
            .audiencePlaceholder(Player.class, "is_logged", (player, queue, ctx) ->
                Tag.selfClosingInserting(AuthMeApi.getInstance().isAuthenticated(player)
                    ? TRUE_COMPONENT
                    : FALSE_COMPONENT)
            )
            .globalPlaceholder("is_player_logged", (queue, ctx) -> {
                final String playerName = queue.popOr("you need to provide a player name").value();
                final Player player = Bukkit.getPlayer(playerName);
                if (player == null) return Tag.selfClosingInserting(FALSE_COMPONENT);
                return Tag.selfClosingInserting(AuthMeApi.getInstance().isAuthenticated(player)
                    ? TRUE_COMPONENT
                    : FALSE_COMPONENT);
            })
            .build();
    }
}
