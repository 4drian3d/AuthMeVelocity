package me.adrianed.authmevelocity.paper;

import me.adrianed.authmevelocity.paper.listeners.AuthMeListener;
import me.adrianed.authmevelocity.paper.listeners.MessageListener;
import me.adrianed.authmevelocity.common.configuration.ConfigurationContainer;
import me.adrianed.authmevelocity.common.configuration.Loader;
import me.adrianed.authmevelocity.common.configuration.PaperConfiguration;
import me.adrianed.authmevelocity.common.MessageType;
import me.adrianed.authmevelocity.common.LibsManager;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import net.byteflux.libby.BukkitLibraryManager;

import java.util.logging.Level;

public final class AuthMeVelocityPlugin extends JavaPlugin {
    private static final String CHANNEL = "authmevelocity:main";

    private ConfigurationContainer<PaperConfiguration> config;

    @Override
    public void onEnable() {
        new LibsManager(new BukkitLibraryManager(this)).loadLibraries();

        try {
            this.config = Loader.loadMainConfig(getDataFolder().toPath(), PaperConfiguration.class);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Could not load config.conf file", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, new MessageListener(this));
        this.getServer().getPluginManager().registerEvents(new AuthMeListener(this), this);

        if (this.getServer().getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            AuthmePlaceholders.getExpansion().register();
        }

        this.getLogger().info("AuthMeVelocity enabled");
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, CHANNEL);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, CHANNEL);

        this.getLogger().info("AuthMeVelocity disabled");
    }

    public void sendMessageToProxy(final Player player, @NotNull MessageType type, @NotNull String playername) {
        @SuppressWarnings("UnstableApiUsage")
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(type.toString());
        out.writeUTF(playername);

        if (player == null) {
            logDebug("MessageToProxy | Null Player, Player Name: "+playername);
            Bukkit.getServer().sendPluginMessage(this, CHANNEL, out.toByteArray());
        } else {
            logDebug("MessageToProxy | Player Present: "+player.getName()+", Player Name: "+playername);
            player.sendPluginMessage(this, CHANNEL, out.toByteArray());
        }
    }

    public void logDebug(String debug) {
        if (config.get().debug()) {
            getLogger().info("[DEBUG] " + debug);
        }
    }
}
