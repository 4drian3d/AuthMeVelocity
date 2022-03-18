package com.glyart.authmevelocity.spigot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.xephi.authme.api.v3.AuthMeApi;
import me.dreamerzero.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;

final class AuthmePlaceholders {
    private AuthmePlaceholders(){}

    private static final Component TRUE = Component.text("true", NamedTextColor.GREEN);
    private static final Component FALSE = Component.text("false", NamedTextColor.RED);

    static Expansion getExpansion(){
        return Expansion.builder("authme")
            .audiencePlaceholder("is_logged", (aud, queue, ctx) -> 
                Tag.selfClosingInserting(AuthMeApi.getInstance().isAuthenticated((Player)aud)
                    ? TRUE
                    : FALSE)
            )
            .globalPlaceholder("is_player_logged", (queue, ctx) -> {
                String playerName = queue.popOr(() -> "you need to provide a player name").value();
                Player player = Bukkit.getPlayer(playerName);
                if(player == null) return Tag.selfClosingInserting(FALSE);
                return Tag.selfClosingInserting(AuthMeApi.getInstance().isAuthenticated(player)
                    ? TRUE
                    : FALSE);
            })
            .build();
    }
}
