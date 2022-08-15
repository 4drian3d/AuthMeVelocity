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
        final Relocation configurateRelocation
            = new Relocation("org{}spongepowered", "me.adrianed.authmevelocity.libs.sponge");
        final Relocation geantyrefRelocation =
            new Relocation("io{}leangen{}geantyref", "me.adrianed.authmevelocity.libs.geantyref");
        final Library hocon = Library.builder()
            .groupId("org{}spongepowered")
            .artifactId("configurate-hocon")
            .version(Constants.CONFIGURATE)
            .id("configurate-hocon")
            .relocate(configurateRelocation)
            .relocate(geantyrefRelocation)
            .build();
        final Library confCore = Library.builder()
            .groupId("org{}spongepowered")
            .artifactId("configurate-core")
            .version(Constants.CONFIGURATE)
            .id("configurate-core")
            .relocate(configurateRelocation)
            .relocate(geantyrefRelocation)
            .build();
        final Library geantyref = Library.builder()
            .groupId("io{}leangen{}geantyref")
            .artifactId("geantyref")
            .version(Constants.GEANTYREF)
            .id("geantyref")
            .relocate(geantyrefRelocation)
            .build();

        manager.loadLibrary(confCore);
        manager.loadLibrary(hocon);
        manager.loadLibrary(geantyref);
    }
}
