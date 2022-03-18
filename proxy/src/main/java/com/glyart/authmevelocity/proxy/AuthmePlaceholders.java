package com.glyart.authmevelocity.proxy;

import com.velocitypowered.api.proxy.Player;

import me.dreamerzero.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;

final class AuthmePlaceholders {
    private AuthmePlaceholders(){}

    private static final Component TRUE = Component.text("true", NamedTextColor.GREEN);
    private static final Component FALSE = Component.text("false", NamedTextColor.RED);

    static Expansion getExpansion(AuthMeVelocityPlugin plugin){
        return Expansion.builder("authme")
            .filter(Player.class)
            .audiencePlaceholder("is_logged", (aud, queue, ctx) -> 
                Tag.selfClosingInserting(plugin.getAPI().isLogged((Player)aud) ? TRUE : FALSE))
            .globalPlaceholder("is_player_logged", (queue, ctx) -> {
                String playerName = queue.popOr(() -> "you need to provide a player").value();
                return Tag.selfClosingInserting(
                    plugin.getProxy().getPlayer(playerName).map(pl -> plugin.getAPI().isLogged(pl)).orElse(false)
                        ? TRUE
                        : FALSE);  
            })
            .build();
    }
}
