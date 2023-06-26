package io.github._4drian3d.authmevelocity.velocity.listener.input;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.protocol.packet.TabCompleteResponse;
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
        if (!(event.getPacket() instanceof final TabCompleteResponse responsePacket)) {
            return null;
        }
        final Player player = event.getPlayer();
        if (player.getProtocolVersion().compareTo(ProtocolVersion.MINECRAFT_1_13) < 0) {
            return null;
        }
        return EventTask.async(() -> {
            if (plugin.isLogged(player)) {
                plugin.logDebug("PacketSendEvent | TabCompleteResponse | Player is already logged");
                return;
            }
            responsePacket.getOffers().clear();
        });
    }
}
