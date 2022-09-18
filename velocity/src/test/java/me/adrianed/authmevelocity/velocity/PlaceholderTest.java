package me.adrianed.authmevelocity.velocity;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import me.adrianed.authmevelocity.velocity.objects.TestPlugin;
import me.adrianed.authmevelocity.velocity.objects.TestProxyServer;
import me.dreamerzero.miniplaceholders.api.MiniPlaceholders;

class PlaceholderTest {
    @Test
    void testRegistration(@TempDir Path path) {
        AuthMePlaceholders.getExpansion(
            new TestPlugin(new TestProxyServer(null), path))
            .register();

        var resolver = MiniPlaceholders.getGlobalPlaceholders();
        assertTrue(resolver.has("authme_is_player_logged"));
    }
}
