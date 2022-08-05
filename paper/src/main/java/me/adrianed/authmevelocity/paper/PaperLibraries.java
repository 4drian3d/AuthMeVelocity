package me.adrianed.authmevelocity.paper;

import me.adrianed.authmevelocity.common.LibsManager;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import net.byteflux.libby.relocation.Relocation;

public class PaperLibraries implements LibsManager {
    private final BukkitLibraryManager manager;

    public PaperLibraries(AuthMeVelocityPlugin plugin) {
        this.manager = new BukkitLibraryManager(plugin);
    }

    @Override
    public void registerRepositories() {
        manager.addMavenCentral();
    }

    @Override
    public void loadLibraries() {
        final Relocation dazzleRelocation
            = new Relocation("space.arim.dazzleconf", "me.adrianed.authmevelocity.libs.dazzleconf");
        final Relocation snakeYamlRelocation
            = new Relocation("pattern", "me.adrianed.authmevelocity.libs.snake");
        final Library dazzleConf = Library.builder()
            .groupId("space.arim.dazzleconf")
            .artifactId("dazzleconf-ext-snakeyaml")
            .version("1.3.0-M1")
            .relocate(dazzleRelocation)
            .build();
        final Library dazzleYaml = Library.builder()
            .groupId("space.arim.dazzleconf")
            .artifactId("dazzleconf-ext-snakeyaml")
            .version("1.3.0-M1")
            .relocate(dazzleRelocation)
            .relocate(snakeYamlRelocation)
            .build();
        final Library snakeYaml = Library.builder()
            .groupId("org.yaml")
            .artifactId("snakeyaml")
            .version("1.30")
            .relocate(dazzleRelocation)
            .relocate(snakeYamlRelocation)
            .build();

        manager.loadLibrary(snakeYaml);
        manager.loadLibrary(dazzleConf);
        manager.loadLibrary(dazzleYaml);
    }
}
