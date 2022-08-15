package me.adrianed.authmevelocity.api.velocity.event;

import java.util.Objects;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.annotation.AwaitingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import org.jetbrains.annotations.NotNull;

/**
 * Event to be executed just before sending a player to another server after login/registration.
 * 
 * <p>AuthMeVelocity will wait for the execution of this event to perform the given action,
 * which means that you can modify the server to which the player will connect
 * or if the player should not be sent to any server after being logged in</p>
 */
@AwaitingEvent
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
    public @NotNull Player player(){
        return this.player;
    }

    /**
     * Obtain the server on which the player is located
     * @return the actual server of the player
     */
    public @NotNull RegisteredServer actualServer(){
        return this.actualserver;
    }

    @Override
    public @NotNull ServerResult getResult() {
        return this.result;
    }

    @Override
    public void setResult(@NotNull ServerResult newResult) {
        this.result = Objects.requireNonNull(newResult);
    }
}
