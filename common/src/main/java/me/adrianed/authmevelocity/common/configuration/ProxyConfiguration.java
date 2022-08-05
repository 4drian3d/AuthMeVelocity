package me.adrianed.authmevelocity.common.configuration;

import java.util.List;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfComments;

public interface ProxyConfiguration {
    @ConfDefault.DefaultStrings({
        "auth1", "auth2"
    })
    @ConfComments({
        "List of login/registration servers"
    })
    List<String> authServers();

    @ConfDefault.DefaultObject("defaultSendOnLogin")
    SendOnLogin sendOnLogin();

    @ConfDefault.DefaultObject("defaultCommands")
    Commands commands();

    @ConfDefault.DefaultObject("defaultEnsureAuth")
    EnsureAuthServer ensureAuthServer();

    public interface SendOnLogin {
        @ConfComments({
            "Send logged in players to another server?"
        })
        @ConfDefault.DefaultBoolean(false)
        boolean sendToServerOnLogin();

        @ConfComments({
            "List of servers to send",
            "One of these servers will be chosen at random"
        })
        @ConfDefault.DefaultStrings({
            "lobby1", "lobby2"
        })
        List<String> teleportServers();
    }

    public interface Commands {
        @ConfComments({
            "Sets the commands that users who have not yet logged in can execute"
        })
        @ConfDefault.DefaultStrings({
            "login", "register", "l", "reg", "email", "captcha"
        })
        List<String> allowedCommands();

        @ConfComments({
            "Sets the message to send in case a non-logged-in player executes an unauthorized command",
            "To deactivate the message, leave it empty"
        })
        @ConfDefault.DefaultString("<red>You cannot execute commands if you are not logged in yet")
        String blockedCommandMessage();
    }

    public interface EnsureAuthServer {
        @ConfComments({
            "Ensure that the first server to which players connect is an auth server"
        })
        @ConfDefault.DefaultBoolean(false)
        boolean ensureFirstServerIsAuthServer();
    }
}
