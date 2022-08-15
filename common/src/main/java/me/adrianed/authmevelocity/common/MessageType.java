package me.adrianed.authmevelocity.common;

import net.kyori.adventure.util.Index;

public enum MessageType {
    LOGIN, REGISTER, LOGOUT, FORCE_UNREGISTER, UNREGISTER;

    // Enum#values is a heavy operation, so... cached MessageType members
    public static final Index<String, MessageType> INDEX
        = Index.create(MessageType::toString, MessageType.values());
}
