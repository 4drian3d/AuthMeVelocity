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

package io.github._4drian3d.authmevelocity.velocity.listener.compat;

import com.github.games647.fastlogin.velocity.event.VelocityFastLoginAutoLoginEvent;
import com.google.inject.Inject;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import io.github._4drian3d.authmevelocity.velocity.listener.Listener;

import java.util.Optional;

public final class FastLoginListener implements Listener<VelocityFastLoginAutoLoginEvent> {
    @Inject
    private ProxyServer proxy;
    @Inject
    private AuthMeVelocityPlugin plugin;

    @Override
    public void register() {
        proxy.getEventManager().register(plugin, VelocityFastLoginAutoLoginEvent.class, this);
    }

    @Override
    public EventTask executeAsync(final VelocityFastLoginAutoLoginEvent event) {
        return EventTask.async(() -> {
            plugin.logDebug(() -> "VelocityFastLoginAutoLoginEvent | Attempt to auto register player");
            final Optional<Player> optionalPlayer = proxy.getPlayer(event.getProfile().getName());
            if (optionalPlayer.isPresent()) {
                final Player player = optionalPlayer.get();
                plugin.logDebug(() -> "VelocityFastLoginAutoLoginEvent | Auto registering player " + player.getUsername());
                plugin.addPlayer(player);
            } else {
                plugin.logDebug(() -> "VelocityFastLoginAutoLoginEvent | Player " + event.getProfile().getName() + " could not be registered");
            }
        });
    }
}
