package me.adrianed.authmevelocity.velocity.listener;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.server.ServerInfo;

import me.adrianed.authmevelocity.velocity.objects.TestContinuation;
import me.adrianed.authmevelocity.velocity.objects.TestPlayer;
import me.adrianed.authmevelocity.velocity.objects.TestPlugin;
import me.adrianed.authmevelocity.velocity.objects.TestProxyServer;
import me.adrianed.authmevelocity.velocity.objects.TestRegisteredServer;

import static org.assertj.core.api.Assertions.*;

class ProxyListenerTest {
    private static ProxyListener listener;
    private static TestPlugin plugin;

    @BeforeAll
    static void prepareListener(@TempDir Path path) {
        plugin = new TestPlugin(new TestProxyServer(null), path);
        listener = new ProxyListener(plugin);
    }

    @Test
    void testSignedPlayerIgnore() {
        plugin.config().setValues(config -> config.advanced().ignoreSignedPlayers(true));
        var player = TestPlayer.SIGNED;
        assertTrue(listener.canBeIgnored(player));
        plugin.config().setValues(config -> config.advanced().ignoreSignedPlayers(false));
    }

    @Test
    void commandExecution_IndefinedServer() {
        var player = TestPlayer.SIGNED;
        var continuation = new TestContinuation();
        var event = new CommandExecuteEvent(player, "command");
        listener.onCommandExecute(event, continuation);

        assertTrue(continuation.resumed());
        assertThat(event.getResult())
            .isEqualTo(CommandResult.denied());
    }

    @Test
    void commandExecution_NotAllowedCommandInAuthServer() {
        var player = new TestPlayer(
            false, Tristate.TRUE, ProtocolVersion.MINECRAFT_1_9_4)
            .setServer(new TestRegisteredServer(
                new ServerInfo("auth1", new InetSocketAddress(25565))));
        var continuation = new TestContinuation();
        var event = new CommandExecuteEvent(player, "command");
        listener.onCommandExecute(event, continuation);

        assertTrue(continuation.resumed());
        assertThat(event.getResult())
            .isEqualTo(CommandResult.denied());
    }

    @Test
    void tabCompleteTest() {
        var player = TestPlayer.OLD;
        var event = new TabCompleteEvent(
            player,
            "random",
            IntStream.range(0, 100)
                .mapToObj(Integer::toString)
                .toList()
        );

        assertThat(event.getSuggestions())
            .isNotEmpty();

        listener.onTabComplete(event);

        assertThat(event.getSuggestions())
            .isNotNull()
            .isEmpty();
    }
}
