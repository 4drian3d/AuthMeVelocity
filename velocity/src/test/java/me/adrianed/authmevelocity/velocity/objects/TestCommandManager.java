package me.adrianed.authmevelocity.velocity.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;

public class TestCommandManager implements CommandManager {
    private final Map<CommandMeta, Command> commands = new HashMap<>();
    private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

    @Override
    public Builder metaBuilder(String alias) {
        return new Builder(alias);
    }

    @Override
    public Builder metaBuilder(BrigadierCommand command) {
        return new Builder(command);
    }

    @Override
    public void register(BrigadierCommand command) {
        commands.put(new Builder(command).build(), command);
        dispatcher.getRoot().addChild(command.getNode());
        
    }

    @Override
    public void register(CommandMeta meta, Command command) {
        commands.put(meta, command);
        var cmd = (BrigadierCommand) command;
        for (var alias : meta.getAliases()) {
            dispatcher.getRoot().addChild(copy(cmd.getNode(), alias));
        }
    }

    public static LiteralCommandNode<CommandSource> copy(
          final LiteralCommandNode<CommandSource> original, final String newName) {

        final var builder = LiteralArgumentBuilder
                .<CommandSource>literal(newName)
                .requires(original.getRequirement())
                .requiresWithContext(original.getContextRequirement())
                .forward(original.getRedirect(), original.getRedirectModifier(), original.isFork())
                .executes(original.getCommand());
        for (var child : original.getChildren()) {
            builder.then(child);
        }
        return builder.build();
    }

    @Override
    public void unregister(String alias) {
        // yeah, i known that this is not correct but shrug
        commands.keySet().removeIf(meta -> meta.getAliases().contains(alias));
    }

    @Override
    public void unregister(CommandMeta meta) {
        commands.keySet().removeIf(meta::equals);
    }

    @Override
    public @Nullable CommandMeta getCommandMeta(String alias) {
        return commands.keySet().stream()
            .filter(meta -> meta.getAliases().contains(alias))
            .findAny()
            .orElse(null);
    }

    public Command getCommand(String alias) {
        return commands.get(getCommandMeta(alias));
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(CommandSource source, String cmdLine) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                dispatcher.execute(cmdLine, source);
                return true;
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> executeImmediatelyAsync(CommandSource source, String cmdLine) {
        return executeAsync(source, cmdLine);
    }

    @Override
    public Collection<String> getAliases() {
        return commands.keySet().stream()
            .map(CommandMeta::getAliases)
            .flatMap(Collection::stream)
            .toList();
    }

    @Override
    public boolean hasCommand(String alias) {
        return commands.keySet().stream()
            .anyMatch(meta -> meta.getAliases().contains(alias));
    }

    public final class Builder implements CommandMeta.Builder {
        private final List<String> aliases = new ArrayList<>();
        private Object plugin;

        private Builder(String command) {
            aliases.add(command);
        }

        private Builder(BrigadierCommand command) {
            aliases.add(command.getNode().getLiteral());
        }

        @Override
        public com.velocitypowered.api.command.CommandMeta.Builder aliases(String... aliases) {
            Collections.addAll(this.aliases, aliases);
            return this;
        }

        @Override
        public com.velocitypowered.api.command.CommandMeta.Builder hint(CommandNode<CommandSource> node) {
            // noop
            return this;
        }

        @Override
        public com.velocitypowered.api.command.CommandMeta.Builder plugin(Object plugin) {
            this.plugin = plugin;
            return this;
        }

        @Override
        public CommandMeta build() {
            return new CommandMeta() {

                @Override
                public Collection<String> getAliases() {
                    return aliases;
                }

                @Override
                public Collection<CommandNode<CommandSource>> getHints() {
                    return Collections.emptyList();
                }

                @Override
                public @Nullable Object getPlugin() {
                    return plugin;
                }
            };
        }

    }
    
}
