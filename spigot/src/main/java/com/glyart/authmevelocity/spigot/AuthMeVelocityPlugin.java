package com.glyart.authmevelocity.spigot;

import com.glyart.authmevelocity.spigot.listeners.AuthMeListener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AuthMeVelocityPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "authmevelocity:main");
        getServer().getPluginManager().registerEvents(new AuthMeListener(this), this);
        
        getLogger().info("AuthMeVelocity enabled.");
    }

    @Override
    public void onDisable() {
        
    }
    
    public void sendLoginToProxy(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("LOGIN");
        out.writeUTF(player.getUniqueId().toString());
        
        player.sendPluginMessage(this, "authmevelocity:main", out.toByteArray());
    }
    
}
