package com.glyart.authmevelocity.spigot;

import com.glyart.authmevelocity.spigot.listeners.AuthMeListener;
import com.glyart.authmevelocity.spigot.listeners.MessageListener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AuthMeVelocityPlugin extends JavaPlugin {
    private static final String CHANNEL = "authmevelocity:main";

    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, new MessageListener());
        this.getServer().getPluginManager().registerEvents(new AuthMeListener(this), this);

        if (this.getServer().getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            AuthmePlaceholders.getExpansion().register();
        }

        this.getSLF4JLogger().info("AuthMeVelocity enabled");
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, CHANNEL);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, CHANNEL);

        this.getSLF4JLogger().info("AuthmeVelocity disabled");
    }

    public void sendMessageToProxy(final Player player, @NotNull MessageType type, @NotNull String playername) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(type.toString());
        out.writeUTF(playername);

        if (player == null) {
            Bukkit.getServer().sendPluginMessage(this, CHANNEL, out.toByteArray());
        } else {
            player.sendPluginMessage(this, CHANNEL, out.toByteArray());
        }
    }
}
