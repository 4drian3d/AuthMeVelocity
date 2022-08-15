package me.adrianed.authmevelocity.api.velocity.event;

import com.velocitypowered.api.proxy.Player;

import org.jetbrains.annotations.NotNull;

/**Event executed in case the player has register itself*/
public record ProxyRegisterEvent(@NotNull Player player) {}
