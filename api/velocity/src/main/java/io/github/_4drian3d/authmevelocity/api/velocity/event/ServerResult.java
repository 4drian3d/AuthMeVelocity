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

package io.github._4drian3d.authmevelocity.api.velocity.event;

import com.velocitypowered.api.event.ResultedEvent.Result;
import com.velocitypowered.api.proxy.server.RegisteredServer;

/**A result that produces a resulting server */
public final class ServerResult implements Result {
    private static final ServerResult DENIED = new ServerResult(null);

    private final RegisteredServer server;
    private ServerResult(RegisteredServer server) {
        this.server = server;
    }

    @Override
    public boolean isAllowed() {
        return this.server != null;
    }

    /**
     * Obtain the resulted server
     * @return the resulted server if is allowed, else null
     */
    public RegisteredServer getServer() {
        return this.server;
    }

    /**
     * Allowed ServerResult
     * @param server the resulted server
     * @return A ServerResult with allowed result and custom server result
     */
    public static ServerResult allowed(RegisteredServer server) {
        return new ServerResult(server);
    }

    /**
     * Denied ServerResult
     * 
     * @return A ServerResult with denied result and null server
     */
    public static ServerResult denied() {
        return DENIED;
    }
    
}
