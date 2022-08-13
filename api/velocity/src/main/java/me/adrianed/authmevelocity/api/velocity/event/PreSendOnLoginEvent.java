package me.adrianed.authmevelocity.api.velocity.event;

import java.util.Objects;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import org.jetbrains.annotations.NotNull;

/**
 * Event to be executed just before sending a player to another server after login/registration.
 * Here you have the ability to deny the event.
 */
public final class PreSendOnLoginEvent implements ResultedEvent<ServerResult> {

    private ServerResult result;
    private final Player player;
    private final RegisteredServer actualserver;

    /**
     * Create a new PreSendOnLoginEvent
     * @param player the player logged
     * @param actualServer the server on which the player is located
     * @param serverToSend the server to which the player will be sent
     */
    public PreSendOnLoginEvent(@NotNull Player player, @NotNull RegisteredServer actualServer, @NotNull RegisteredServer serverToSend){
        this.player = player;
        this.actualserver = actualServer;
        result = ServerResult.allowed(serverToSend);
    }

    /**
     * Obtain the logged player
     * @return the player
     */
    public @NotNull Player getPlayer(){
        return this.player;
    }

    /**
     * Obtain the server on which the player is located
     * @return the actual server of the player
     */
    public @NotNull RegisteredServer getActualServer(){
        return this.actualserver;
    }

    /**
     * Get the result of the event
     */
    @Override
    public @NotNull ServerResult getResult() {
        return this.result;
    }

    /**
     * Set the result of the event
     * @param newresult the new result
     */
    @Override
    public void setResult(@NotNull ServerResult newResult) {
        this.result = Objects.requireNonNull(newResult);
    }
}
