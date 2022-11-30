package me.adrianed.authmevelocity.paper.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import me.adrianed.authmevelocity.api.paper.event.LoginByProxyEvent;
import me.adrianed.authmevelocity.paper.AuthMeVelocityPlugin;

import fr.xephi.authme.api.v3.AuthMeApi;

public class MessageListener implements PluginMessageListener {
    private final AuthMeVelocityPlugin plugin;

    public MessageListener(AuthMeVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String identifier, @NotNull Player player, byte @NotNull [] bytes) {
        if (!identifier.equals("authmevelocity")) {
            plugin.logDebug("PluginMessage | Not AuthMeVelocity identifier");
            return;
        }

        plugin.logDebug("PluginMessage | AuthMeVelocity identifier");

        @SuppressWarnings("UnstableApiUsage")
        final ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        final String subChannel = input.readUTF();

        if ("main".equals(subChannel)) {
            plugin.logDebug("PluginMessage | Main Subchannel");
            final String msg = input.readUTF();
            if ("LOGIN".equals(msg)) {
                plugin.logDebug("PluginMessage | Login Message");
                new LoginByProxyEvent(player).callEvent();
                AuthMeApi.getInstance().forceLogin(player);
            }
        }
    }
}
