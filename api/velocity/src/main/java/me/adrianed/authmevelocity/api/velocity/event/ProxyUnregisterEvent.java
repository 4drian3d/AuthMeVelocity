package me.adrianed.authmevelocity.api.velocity.event;

import com.velocitypowered.api.proxy.Player;

import org.jetbrains.annotations.NotNull;

/**Event executed in case the player has unregistered itself*/
public record ProxyUnregisterEvent(@NotNull Player player) {}
