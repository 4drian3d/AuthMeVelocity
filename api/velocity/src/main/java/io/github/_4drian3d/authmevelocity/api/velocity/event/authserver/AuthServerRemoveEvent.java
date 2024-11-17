package io.github._4drian3d.authmevelocity.api.velocity.event.authserver;

import org.jetbrains.annotations.NotNull;

/**
 * Event to execute when removing an auth server
 *
 * @param server the server to remove
 */
public record AuthServerRemoveEvent(@NotNull String server) {
}
