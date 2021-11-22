package com.glyart.authmevelocity.spigot;

import com.glyart.authmevelocity.spigot.listeners.AuthMeListener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AuthMeVelocityPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "authmevelocity:main");
        this.getServer().getPluginManager().registerEvents(new AuthMeListener(this), this);

        this.getLogger().info("AuthMeVelocity enabled.");
    }

    public void sendLoginToProxy(@NotNull final Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("LOGIN");

        player.sendPluginMessage(this, "authmevelocity:main", out.toByteArray());
    }
}
