package me.adrianed.authmevelocity.api.velocity.event;

import com.velocitypowered.api.event.ResultedEvent.Result;
import com.velocitypowered.api.proxy.server.RegisteredServer;

/**A result that produces a resulting server */
public record ServerResult(RegisteredServer server) implements Result {
    private static final ServerResult DENIED = new ServerResult(null);

    @Override
    public boolean isAllowed() {
        return server != null;
    }

    /**
     * Allowed ServerResult
     * @param server the resulted server
     * @return A ServerResult with allowed result and custom server result
     */
    public static final ServerResult allowed(RegisteredServer server) {
        return new ServerResult(server);
    }

    /**
     * Denied ServerResult
     * 
     * @return A ServerResult with denied result and null server
     */
    public static final ServerResult denied() {
        return DENIED;
    }
    
}
