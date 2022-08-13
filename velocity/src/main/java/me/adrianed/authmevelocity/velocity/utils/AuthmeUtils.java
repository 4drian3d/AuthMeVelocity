package me.adrianed.authmevelocity.velocity.utils;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

public class AuthmeUtils {
    //Origin: https://github.com/4drian3d/ChatRegulator/blob/main/src/main/java/me/dreamerzero/chatregulator/utils/CommandUtils.java#L71
    /**
     * Get the first argument of a string
     * @param string the string
     * @return the first argument
     */
    public static @NotNull String getFirstArgument(final @NotNull String string){
        final int index = Objects.requireNonNull(string).indexOf(' ');
        if (index == -1) {
            return string;
        }
        return string.substring(0, index);
    }
    private AuthmeUtils() {}
}
