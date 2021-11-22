package com.glyart.authmevelocity.spigot;

import com.glyart.authmevelocity.spigot.listeners.AuthMeListener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AuthMeVelocityPlugin extends JavaPlugin {
    private static final String CHANNEL = "authmevelocity:main";
    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        this.getServer().getPluginManager().registerEvents(new AuthMeListener(this), this);

        this.getLogger().info("AuthMeVelocity enabled.");
    }

    public void sendMessageToProxy(@NotNull final Player player, MessageType type) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(type.toString());

        player.sendPluginMessage(this, CHANNEL, out.toByteArray());
    }
}
