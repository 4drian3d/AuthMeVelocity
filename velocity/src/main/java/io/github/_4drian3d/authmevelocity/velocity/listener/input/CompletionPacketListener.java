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
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.protocol.packet.TabCompleteResponsePacket;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import io.github._4drian3d.authmevelocity.velocity.listener.Listener;
import io.github._4drian3d.vpacketevents.api.event.PacketSendEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class CompletionPacketListener implements Listener<PacketSendEvent> {
    @Inject
    private EventManager eventManager;
    @Inject
    private AuthMeVelocityPlugin plugin;

    @Override
    public void register() {
        eventManager.register(plugin, PacketSendEvent.class, this);
    }

    @Override
    public @Nullable EventTask executeAsync(final PacketSendEvent event) {
        if (!(event.getPacket() instanceof final TabCompleteResponsePacket responsePacket)) {
            return null;
        }
        final Player player = event.getPlayer();
        return EventTask.async(() -> {
            if (plugin.isLogged(player)) {
                plugin.logDebug(() -> "PacketSendEvent | TabCompleteResponse | Player " + player.getUsername() + " is already logged");
                return;
            }
            responsePacket.getOffers().clear();
        });
    }
}
