package io.github._4drian3d.authmevelocity.api.velocity.event.authserver;

import org.jetbrains.annotations.NotNull;

/**
 * Event to execute when adding an auth server.
 *
 * @param server the server added
 */
public record AuthServerAddEvent(@NotNull String server) {
}
