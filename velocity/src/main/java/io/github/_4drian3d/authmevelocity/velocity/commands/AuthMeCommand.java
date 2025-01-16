/*
 * Copyright (C) 2025 AuthMeVelocity Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github._4drian3d.authmevelocity.velocity.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;

import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

public class AuthMeCommand {
    @Inject
    private AuthMeVelocityPlugin plugin;
    @Inject
    private CommandManager manager;
    @Inject
    private Logger logger;

    public void register() {
        final var command = LiteralArgumentBuilder.<CommandSource>literal("authmevelocity")
                .requires(src -> src.hasPermission("authmevelocity.commands"))
                .then(LiteralArgumentBuilder.<CommandSource>literal("reload")
                        .executes(cmd -> {
                            final CommandSource source = cmd.getSource();
                            plugin.config().reload().handleAsync((v, ex) -> {
                                if (ex == null) {
                                    plugin.setAuthServers(plugin.config().get().authServers());
                                    plugin.sendInfoMessage();
                                    source.sendMessage(MiniMessage.miniMessage().deserialize(
                                            "<aqua>AuthmeVelocity <green>has been successfully reloaded"));
                                } else {
                                    source.sendMessage(MiniMessage.miniMessage().deserialize(
                                            "<dark_red>There was an error while reloading the configuration. <red>Check the server console"));
                                    logger.error(ex.getMessage(), ex.getCause());
                                }
                                return null;
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                ).build();

        final BrigadierCommand brigadier = new BrigadierCommand(command);
        final CommandMeta meta = manager.metaBuilder(brigadier)
                .plugin(plugin)
                .aliases("vauthme", "authmev")
                .build();

        manager.register(meta, brigadier);

    }
}
