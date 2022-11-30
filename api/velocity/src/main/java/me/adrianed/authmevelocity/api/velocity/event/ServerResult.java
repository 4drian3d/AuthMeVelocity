package me.adrianed.authmevelocity.api.velocity.event;

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
