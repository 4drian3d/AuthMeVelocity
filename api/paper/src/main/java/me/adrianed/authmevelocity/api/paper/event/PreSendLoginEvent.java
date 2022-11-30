package me.adrianed.authmevelocity.api.paper.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event executed before a player is sent to another server after being logged in
 * <p>Here you have the possibility to cancel the player's sending</p>
*/
public final class PreSendLoginEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;

    /**
     * Creates a new PreSendLoginEvent
     * @param player the player to be sended
     */
    public PreSendLoginEvent(@NotNull Player player) {
        super(player, !Bukkit.isPrimaryThread());
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Obtain the handlerlist of this event
     * @return the handlerlist
     */
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
