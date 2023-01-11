package me.adrianed.authmevelocity.api.paper.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event to be executed in case a player who has already logged
 * into a previously configured server enters the server.
 * 
 * <p>AuthMeVelocity will automatically login this player.</p>
 */
public final class LoginByProxyEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Creates a new LoginByProxyEvent
     * @param who the player to be logged in
     */
    public LoginByProxyEvent(@NotNull Player who) {
        super(who);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Obtain the handlerlist of this event
     * @return the handlerlist
     */
    @SuppressWarnings("unused")
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}
