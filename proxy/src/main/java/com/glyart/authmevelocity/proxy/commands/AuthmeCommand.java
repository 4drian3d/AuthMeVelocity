package com.glyart.authmevelocity.proxy.commands;

import com.glyart.authmevelocity.proxy.AuthMeVelocityPlugin;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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
                        source.sendMessage(Component.text("AuthmeVelocity has been successfully reloaded", NamedTextColor.GREEN));
                    } else {
                        source.sendMessage(Component.text(
                            "There was an error while reloading the configuration. Check the server console", NamedTextColor.DARK_RED));
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
