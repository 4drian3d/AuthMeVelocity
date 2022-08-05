package me.adrianed.authmevelocity.common.configuration;

import org.slf4j.Logger;

import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.CommentMode;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;
import space.arim.dazzleconf.helper.ConfigurationHelper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class Loader<C> {
    private final ConfigurationHelper<C> configHelper;
	private volatile C configData;
    private final Logger logger;

	private Loader(ConfigurationHelper<C> configHelper, Logger logger) {
		this.configHelper = configHelper;
		this.logger = logger;
	}

	public static <C> Loader<C> create(Path configFolder, String fileName, Class<C> configClass, Logger logger) {
		SnakeYamlOptions yamlOptions = new SnakeYamlOptions.Builder()
				.commentMode(CommentMode.alternativeWriter())
				.build();
		ConfigurationFactory<C> configFactory = SnakeYamlConfigurationFactory.create(
				configClass,
				ConfigurationOptions.defaults(),
				yamlOptions);
		return new Loader<>(new ConfigurationHelper<>(configFolder, fileName, configFactory), logger);
	}

	public boolean reloadConfig() {
		try {
			configData = configHelper.reloadConfigData();
			return true;
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		} catch (ConfigFormatSyntaxException ex) {
			configData = configHelper.getFactory().loadDefaults();
			logger.error("The yaml syntax in your configuration is invalid. "
					+ "Check your YAML syntax with a tool such as https://yaml-online-parser.appspot.com/", ex);
			return false;
		} catch (InvalidConfigException ex) {
			configData = configHelper.getFactory().loadDefaults();
			logger.error("One of the values in your configuration is not valid. "
					+ "Check to make sure you have specified the right data types.", ex);
			return false;
		}
	}

	public C getConfig() {
		C configData = this.configData;
		if (configData == null) {
			throw new IllegalStateException("Configuration has not been loaded yet");
		}
		return configData;
	}
}
