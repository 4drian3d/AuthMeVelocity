package me.adrianed.authmevelocity.velocity.utils;

public record Pair<O>(String string, O object) {
    public boolean isPresent() {
        return this.object != null;
    }

    public boolean isEmpty() {
        return object == null;
    }

    public static <O> Pair<O> of(String string, O object) {
        return new Pair<>(string, object);
    }
}
