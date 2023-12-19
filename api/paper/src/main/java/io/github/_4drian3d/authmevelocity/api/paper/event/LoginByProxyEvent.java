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

package io.github._4drian3d.authmevelocity.api.paper.event;

import org.bukkit.Bukkit;
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
        super(who, !Bukkit.isPrimaryThread());
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
