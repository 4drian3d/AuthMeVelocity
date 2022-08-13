package me.adrianed.authmevelocity.api.velocity.event;

import com.velocitypowered.api.event.ResultedEvent.Result;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public record ServerResult(boolean result, RegisteredServer server) implements Result {
    private static final ServerResult DENIED = new ServerResult(false, null);

    @Override
    public boolean isAllowed() {
        return result;
    }

    public static final ServerResult allowed(RegisteredServer server) {
        return new ServerResult(true, server);
    }

    public static final ServerResult denied() {
        return DENIED;
    }
    
}
