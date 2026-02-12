/*
 * Copyright (C) 2026 AuthMeVelocity Contributors
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

package io.github._4drian3d.authmevelocity.api.paper.event;

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
     * @param player the player to be sent
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
    @SuppressWarnings("unused")
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
