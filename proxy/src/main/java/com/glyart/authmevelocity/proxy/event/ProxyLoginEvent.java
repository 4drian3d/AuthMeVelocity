package com.glyart.authmevelocity.proxy.event;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

/**
 * Event executed in case the player is successfully logged in
 */
public record ProxyLoginEvent(Player player, RegisteredServer server) {}
