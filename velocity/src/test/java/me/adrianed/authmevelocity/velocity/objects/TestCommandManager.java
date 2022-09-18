package me.adrianed.authmevelocity.velocity.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.mojang.brigadier.tree.CommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;

public class TestCommandManager implements CommandManager {
    private final List<CommandMeta> metas = new ArrayList<>();

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
        metas.add(new Builder(command).build());
    }

    @Override
    public void register(CommandMeta meta, Command command) {
        metas.add(meta);
    }

    @Override
    public void unregister(String alias) {
        // yeah, i known that this is not correct but shrug
        metas.removeIf(meta -> meta.getAliases().contains(alias));
    }

    @Override
    public void unregister(CommandMeta meta) {
        metas.removeIf(meta::equals);
    }

    @Override
    public @Nullable CommandMeta getCommandMeta(String alias) {
        return metas.stream()
            .filter(meta -> meta.getAliases().contains(alias))
            .findAny()
            .orElse(null);
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(CommandSource source, String cmdLine) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> executeImmediatelyAsync(CommandSource source, String cmdLine) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Collection<String> getAliases() {
        return metas.stream()
            .map(CommandMeta::getAliases)
            .flatMap(Collection::stream)
            .toList();
    }

    @Override
    public boolean hasCommand(String alias) {
        return metas.stream()
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
