package com.glyart.authmevelocity.proxy.event;

import com.velocitypowered.api.proxy.Player;

import org.jetbrains.annotations.NotNull;

public record ProxyLogoutEvent(@NotNull Player player) {}
