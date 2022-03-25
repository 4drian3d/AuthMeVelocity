package com.nicecodes.objects;

import com.moandjiezana.toml.Toml;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class TomlFile {
    private final Toml toml;

    @SneakyThrows
    public TomlFile(Path path, @NotNull String name) {

        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }

        Path configPath = path.resolve(name);

        if (!Files.exists(configPath)) {
            try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(name)) {
                Files.copy(in, configPath);
            }
        }

        toml = new Toml().read(Files.newInputStream(configPath));
    }

    @Nullable
    public Toml getToml() {
        return toml;
    }
}