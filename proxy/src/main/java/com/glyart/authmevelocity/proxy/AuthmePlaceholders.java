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
            .audiencePlaceholder("is_logged", (aud, queue, ctx) -> 
                Tag.selfClosingInserting(plugin.getAPI().isLogged((Player)aud) ? TRUE_COMPONENT : FALSE_COMPONENT))
            .globalPlaceholder("is_player_logged", (queue, ctx) -> {
                String playerName = queue.popOr(() -> "you need to provide a player").value();
                return Tag.selfClosingInserting(
                    plugin.getProxy().getPlayer(playerName).map(pl -> plugin.getAPI().isLogged(pl)).isPresent()
                        ? TRUE_COMPONENT
                        : FALSE_COMPONENT
                    );
            })
            .build();
    }
}
