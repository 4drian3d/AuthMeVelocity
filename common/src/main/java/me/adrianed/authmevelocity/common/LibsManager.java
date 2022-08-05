package me.adrianed.authmevelocity.common;

import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;
import net.byteflux.libby.relocation.Relocation;


public final class LibsManager {
    private final LibraryManager manager;

    public LibsManager(LibraryManager manager) {
        this.manager = manager;
        manager.addMavenCentral();
    }

    public void loadLibraries() {
        final String dazzlePackage = new String(new char[] {
            's','p','a','c','e','.','a','r','i','m','.','d','a','z','z','l','e','c','o','n','f'});
        final Relocation dazzleRelocation
            = new Relocation(dazzlePackage, "me.adrianed.authmevelocity.libs.dazzleconf");
        final Relocation snakeYamlRelocation
            = new Relocation("org.yaml.snakeyaml", "me.adrianed.authmevelocity.libs.snakeyaml");
        final Library dazzleConf = Library.builder()
            .groupId(dazzlePackage)
            .artifactId("dazzleconf-core")
            .version("1.3.0-M1")
            .relocate(dazzleRelocation)
            .build();
        final Library dazzleYaml = Library.builder()
            .groupId(dazzlePackage)
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
