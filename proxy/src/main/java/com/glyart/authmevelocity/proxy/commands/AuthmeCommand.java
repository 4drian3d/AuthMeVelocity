package com.glyart.authmevelocity.proxy.commands;

import com.glyart.authmevelocity.proxy.AuthMeVelocityPlugin;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class AuthmeCommand {
    private AuthmeCommand() {}

    public static void register(AuthMeVelocityPlugin plugin, CommandManager manager) {
        LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder.<CommandSource>literal("authmevelocity")
            .requires(src -> src.hasPermission("authmevelocity.commands"))
            .then(LiteralArgumentBuilder.<CommandSource>literal("reload")
                .executes(cmd -> {
                    CommandSource source = cmd.getSource();
                    if (plugin.reload()) {
                        plugin.sendInfoMessage();
                        source.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<aqua>AuthmeVelocity <green>has been successfully reloaded"));
                    } else {
                        source.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<dark_red>There was an error while reloading the configuration. <red>Check the server console"));
                    }
                    return Command.SINGLE_SUCCESS;
                })
            ).build();

        BrigadierCommand brigadier = new BrigadierCommand(command);
        CommandMeta meta = manager.metaBuilder(brigadier)
            .plugin(plugin)
            .aliases("vauthme", "authmev")
            .build();

        manager.register(meta, brigadier);
        
    }
}
