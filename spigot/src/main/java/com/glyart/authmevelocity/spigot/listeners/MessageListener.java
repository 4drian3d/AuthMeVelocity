package com.glyart.authmevelocity.spigot.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import fr.xephi.authme.api.v3.AuthMeApi;

public class MessageListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String identifier, @NotNull Player player, @NotNull byte[] bytes) {
        if (!identifier.equals("authmevelocity")) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        String subchannel = input.readUTF();
        if ("main".equals(subchannel)) {
            String msg = input.readUTF();
            if ("LOGIN".equals(msg)) {
                AuthMeApi.getInstance().forceLogin(player);
            }
        }
    }
}
