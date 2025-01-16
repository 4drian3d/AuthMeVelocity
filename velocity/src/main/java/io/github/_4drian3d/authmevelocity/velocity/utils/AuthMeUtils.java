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

package io.github._4drian3d.authmevelocity.velocity.utils;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.authmevelocity.common.enums.SendMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class AuthMeUtils {
    //Origin: https://github.com/4drian3d/ChatRegulator/blob/main/src/main/java/me/dreamerzero/chatregulator/utils/CommandUtils.java#L71
    /**
     * Get the first argument of a string
     * @param string the string
     * @return the first argument
     */
    public static @NotNull String getFirstArgument(final @NotNull String string){
        final int index = Objects.requireNonNull(string).indexOf(' ');
        if (index == -1) {
            return string;
        }
        return string.substring(0, index);
    }

    private static final Random RANDOM = new Random();

    public static Pair<RegisteredServer> serverToSend(SendMode sendMode, ProxyServer proxy, List<String> servers, int attempts) {
        return switch(sendMode) {
            case TO_FIRST -> {
                Optional<RegisteredServer> sv;
                for (final String st : servers) {
                    sv = proxy.getServer(st);
                    if (sv.isPresent()) yield Pair.of(st, sv.get());
                }
                yield Pair.of(null, null);
            }
            case TO_EMPTIEST_SERVER -> {
                RegisteredServer emptiest = null;
                Optional<RegisteredServer> optional = Optional.empty();
                for (final String st : servers) {
                    optional = proxy.getServer(st);
                    if (optional.isPresent()) {
                        RegisteredServer actualsv = optional.get();
                        int actualConnected = actualsv.getPlayersConnected().size();
                        if (actualConnected == 0) {
                            yield Pair.of(st, actualsv);
                        } 
                        if (emptiest == null || actualConnected < emptiest.getPlayersConnected().size()) {
                            emptiest = actualsv;
                        }
                    }
                }
                yield Pair.of(optional.map(sv -> sv.getServerInfo().getName()).orElse(null), emptiest);
            }
            case RANDOM -> {
                Optional<RegisteredServer> server;
                if (servers.size() == 1) {
                    server = proxy.getServer(servers.get(0));
                    // It is nonsense to make so many attempts if there are a single server
                    yield Pair.of(
                        server.map(sv -> sv.getServerInfo().getName()).orElse(null),
                        server.orElse(null));
                }
                for (int i = 0; i < attempts; i++) {
                    int value = RANDOM.nextInt(servers.size());
                    server = proxy.getServer(servers.get(value));
                    if (server.isPresent()) {
                        yield Pair.of(server.get().getServerInfo().getName(), server.get());
                    }
                }
                yield Pair.of(null, null);
            }
        };
    }
    private AuthMeUtils() {}
}
