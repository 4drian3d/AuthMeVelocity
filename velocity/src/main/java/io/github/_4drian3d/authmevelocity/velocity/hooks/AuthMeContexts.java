package io.github._4drian3d.authmevelocity.velocity.hooks;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class AuthMeContexts implements ContextCalculator<Player> {
    @Inject
    private AuthMeVelocityPlugin plugin;

    @Override
    public void calculate(final @NonNull Player target, final @NonNull ContextConsumer consumer) {
        consumer.accept(ImmutableContextSet.builder()
                .add("logged", Boolean.toString(plugin.isLogged(target)))
                .add("isInAuthServer", Boolean.toString(plugin.isInAuthServer(target)))
                .build());
    }

    @Override
    public @NonNull ContextSet estimatePotentialContexts() {
        return ImmutableContextSet.builder()
                .add("logged", "true")
                .add("logged", "false")
                .add("isInAuthServer", "true")
                .add("isInAuthServer", "false")
                .build();
    }

    public void register() {
        final LuckPerms luckPerms = LuckPermsProvider.get();
        luckPerms.getContextManager().registerCalculator(this);
    }
}
