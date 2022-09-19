package me.adrianed.authmevelocity.velocity.commands;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.velocitypowered.api.command.BrigadierCommand;

import me.adrianed.authmevelocity.velocity.objects.TestCommandManager;
import me.adrianed.authmevelocity.velocity.objects.TestPlayer;
import me.adrianed.authmevelocity.velocity.objects.TestPlugin;
import me.adrianed.authmevelocity.velocity.objects.TestProxyServer;

import static org.assertj.core.api.Assertions.*;

class CommandTest {
    @Test
    void testRegistration() {
        var manager = new TestCommandManager();
        AuthmeCommand.register(null, manager);
        assertThat(manager.getCommandMeta("authmevelocity"))
            .isNotNull();
        assertTrue(manager.hasCommand("authmev"));
        assertTrue(manager.hasCommand("authmevelocity"));
    }

    @Test
    void testTheTestDispatcher() {
        var manager = new TestCommandManager();
        // There is none command registered
        assertThat(manager.executeAsync(TestPlayer.SIGNED, "authmevelocity reload"))
            .failsWithin(Duration.ofNanos(10));
    }

    @Test
    void testExecution(@TempDir Path path) {
        var manager = new TestCommandManager();
        var plugin = new TestPlugin(new TestProxyServer(manager), path);
        AuthmeCommand.register(plugin, manager);

        var command = (BrigadierCommand) manager.getCommand("authmevelocity");
        var node = command.getNode();
        assertTrue(node.canUse(TestPlayer.SIGNED));

        assertThat(manager.executeAsync(TestPlayer.SIGNED, "authmevelocity reload")
            .thenCombine(manager.executeAsync(TestPlayer.SIGNED, "authmev reload"), (r, r2) -> r && r2))
            .succeedsWithin(Duration.ofNanos(50)) //fazzzst
            .isEqualTo(true);
    }
}
