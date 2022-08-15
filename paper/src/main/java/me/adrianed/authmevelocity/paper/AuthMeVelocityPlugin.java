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

public final class AuthMeVelocityPlugin extends JavaPlugin {
    private static final String CHANNEL = "authmevelocity:main";

    private ConfigurationContainer<PaperConfiguration> config;

    @Override
    public void onEnable() {
        final LibsManager libraries
            = new LibsManager(new BukkitLibraryManager(this));
        libraries.loadLibraries();

        this.config = Loader.loadMainConfig(getDataFolder().toPath(), PaperConfiguration.class, getSLF4JLogger());

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, new MessageListener(this));
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
            logDebug("MessageToProxy | Null Player, Player Name: "+playername);
            Bukkit.getServer().sendPluginMessage(this, CHANNEL, out.toByteArray());
        } else {
            logDebug("MessageToProxy | Player Present: "+player.getName()+", Player Name: "+playername);
            player.sendPluginMessage(this, CHANNEL, out.toByteArray());
        }
    }

    public ConfigurationContainer<PaperConfiguration> config() {
        return this.config;
    }

    public void logDebug(String debug) {
        if (config.get().debug()) {
            getSLF4JLogger().info("[DEBUG] {}", debug);
        }
    }
}
