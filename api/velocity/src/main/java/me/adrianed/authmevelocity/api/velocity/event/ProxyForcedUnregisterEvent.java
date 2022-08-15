package me.adrianed.authmevelocity.api.velocity.event;

import com.velocitypowered.api.proxy.Player;

import org.jetbrains.annotations.Nullable;

/**Event executed in case a player is forced unregister by a server operator*/
public record ProxyForcedUnregisterEvent(@Nullable Player player) {}
