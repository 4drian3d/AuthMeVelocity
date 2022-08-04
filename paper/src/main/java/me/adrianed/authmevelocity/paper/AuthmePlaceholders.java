package me.adrianed.authmevelocity.paper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.xephi.authme.api.v3.AuthMeApi;
import me.dreamerzero.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.minimessage.tag.Tag;

import static me.dreamerzero.miniplaceholders.api.utils.Components.*;

final class AuthmePlaceholders {
    private AuthmePlaceholders(){}

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
                if(player == null) return Tag.selfClosingInserting(FALSE_COMPONENT);
                return Tag.selfClosingInserting(AuthMeApi.getInstance().isAuthenticated(player)
                    ? TRUE_COMPONENT
                    : FALSE_COMPONENT);
            })
            .build();
    }
}
