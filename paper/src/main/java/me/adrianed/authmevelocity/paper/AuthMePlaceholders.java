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

package me.adrianed.authmevelocity.paper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.xephi.authme.api.v3.AuthMeApi;
import me.dreamerzero.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.minimessage.tag.Tag;

import static me.dreamerzero.miniplaceholders.api.utils.Components.*;

final class AuthMePlaceholders {
    private AuthMePlaceholders() {}

    static Expansion getExpansion(){
        return Expansion.builder("authme")
            .filter(Player.class)
            .audiencePlaceholder("is_logged", (aud, queue, ctx) -> 
                Tag.selfClosingInserting(AuthMeApi.getInstance().isAuthenticated((Player)aud)
                    ? TRUE_COMPONENT
                    : FALSE_COMPONENT)
            )
            .globalPlaceholder("is_player_logged", (queue, ctx) -> {
                String playerName = queue.popOr("you need to provide a player name").value();
                Player player = Bukkit.getPlayer(playerName);
                if (player == null) return Tag.selfClosingInserting(FALSE_COMPONENT);
                return Tag.selfClosingInserting(AuthMeApi.getInstance().isAuthenticated(player)
                    ? TRUE_COMPONENT
                    : FALSE_COMPONENT);
            })
            .build();
    }
}
