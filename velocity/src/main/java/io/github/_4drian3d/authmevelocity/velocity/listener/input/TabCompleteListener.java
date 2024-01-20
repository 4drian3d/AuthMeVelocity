/*
 * Copyright (C) 2024 AuthMeVelocity Contributors
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

package io.github._4drian3d.authmevelocity.velocity.listener.input;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import io.github._4drian3d.authmevelocity.velocity.listener.Listener;

public class TabCompleteListener implements Listener<TabCompleteEvent> {
    @Inject
    private AuthMeVelocityPlugin plugin;
    @Inject
    private EventManager eventManager;

    @Override
    public void register() {
        eventManager.register(plugin, TabCompleteEvent.class, PostOrder.FIRST, this);
    }

    @Override
    public EventTask executeAsync(final TabCompleteEvent event) {
        return EventTask.async(() -> {
            if (plugin.isLogged(event.getPlayer())) {
                plugin.logDebug("TabCompleteEvent | Player " + event.getPlayer().getUsername() + " is already logged");
                return;
            }

            final String command = event.getPartialMessage();
            for (final String allowed : plugin.config().get().commands().allowedCommands()) {
                if (allowed.startsWith(command)) {
                    return;
                }
            }

            plugin.logDebug("TabCompleteEvent | " + event.getPlayer().getUsername() + " | Not allowed tab-completion");
            event.getSuggestions().clear();
        });
    }
}
