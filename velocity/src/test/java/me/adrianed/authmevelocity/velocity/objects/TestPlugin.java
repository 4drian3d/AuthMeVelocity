package me.adrianed.authmevelocity.velocity.objects;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.velocitypowered.api.proxy.ProxyServer;

import me.adrianed.authmevelocity.common.configuration.Loader;
import me.adrianed.authmevelocity.common.configuration.ProxyConfiguration;
import me.adrianed.authmevelocity.velocity.AuthMeVelocityPlugin;

public final class TestPlugin extends AuthMeVelocityPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestPlugin.class);

    public TestPlugin(ProxyServer proxy, Path dataDirectory) {
        super(proxy, LOGGER, dataDirectory, null);
        this.config = Loader.loadMainConfig(dataDirectory, ProxyConfiguration.class, LOGGER);
    }
}
