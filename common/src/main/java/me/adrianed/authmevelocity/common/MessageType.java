package me.adrianed.authmevelocity.common;

import net.kyori.adventure.util.Index;

public enum MessageType {
    LOGIN, REGISTER, LOGOUT, FORCE_UNREGISTER, UNREGISTER;

    public static final Index<String, MessageType> INDEX = Index.create((value) -> value.toString(), MessageType.values());
}
