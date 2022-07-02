package com.glyart.authmevelocity.proxy;

import com.velocitypowered.api.proxy.Player;

import me.dreamerzero.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.minimessage.tag.Tag;

import static me.dreamerzero.miniplaceholders.api.utils.Components.*;

final class AuthmePlaceholders {
    private AuthmePlaceholders(){}

    static Expansion getExpansion(AuthMeVelocityPlugin plugin){
        return Expansion.builder("authme")
            .filter(Player.class)
            // Logged Placeholders
            .audiencePlaceholder("is_logged", (aud, queue, ctx) -> 
                Tag.selfClosingInserting(plugin.getAPI().isLogged((Player)aud) ? TRUE_COMPONENT : FALSE_COMPONENT))
            .globalPlaceholder("is_player_logged", (queue, ctx) -> {
                String playerName = queue.popOr(() -> "you need to provide a player").value();
                return Tag.selfClosingInserting(
                    plugin.getProxy().getPlayer(playerName)
                        .map(plugin.getAPI()::isLogged)
                        .orElse(false) ? TRUE_COMPONENT : FALSE_COMPONENT
                    );
            })
            // In Auth Server placeholders
            .audiencePlaceholder("in_auth_server", (aud, queue, ctx) -> 
                Tag.selfClosingInserting(plugin.getAPI().isInAuthServer((Player)aud) ? TRUE_COMPONENT : FALSE_COMPONENT))
            .globalPlaceholder("player_in_auth_server", (queue, ctx) -> {
                String playerName = queue.popOr(() -> "you need to provide a player").value();
                return Tag.selfClosingInserting(
                    plugin.getProxy().getPlayer(playerName)
                        .map(plugin.getAPI()::isInAuthServer)
                        .orElse(false) ? TRUE_COMPONENT : FALSE_COMPONENT
                    );
            })
            .build();
    }
}
