package com.nicecodes.commands;

import com.glyart.authmevelocity.proxy.AuthMeVelocityPlugin;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HubCommand implements SimpleCommand {
    private final AuthMeVelocityPlugin plugin;

    public HubCommand(AuthMeVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        var commandSource = invocation.source();

        if (!(commandSource instanceof Player)) {
            sendMessage(commandSource, "notPlayer");
            return;
        }

        Player player = (Player) commandSource;
        List<RegisteredServer> lobbies = plugin.getServers();

        if (lobbies.isEmpty()) {
            sendMessage(player, "noServers");
            return;
        }

        AtomicBoolean isFound = new AtomicBoolean(false);

        for (int i = 0; i < lobbies.size(); i++) {
            if (isFound.get()) {
                return;
            }

            RegisteredServer rs = lobbies.get(i);

            var playerCurrentServerOptional = player.getCurrentServer();
            if (playerCurrentServerOptional.isPresent() && playerCurrentServerOptional.get().getServer().equals(rs)) {
                sendMessage(player, "alreadyConnected");
                return;
            }

            player.createConnectionRequest(rs).connect().whenComplete((result, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                }

                if (result.isSuccessful()) {
                    sendMessage(player, "moveSuccessful");
                    isFound.set(true);
                } else {
                    sendMessage(player, "error");
                }

            });
        }
    }

    private void sendMessage(CommandSource commandSource, String path) {
        String message = plugin.getHubToml().getString(path);

        if (message == null || message.isBlank()) {
            return;
        }

        commandSource.sendMessage(MiniMessage.miniMessage().deserialize(message));

    }
}
