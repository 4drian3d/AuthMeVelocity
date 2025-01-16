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
