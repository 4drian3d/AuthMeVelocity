package me.adrianed.authmevelocity.velocity.commands;


import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import me.adrianed.authmevelocity.velocity.objects.TestCommandManager;
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
}
