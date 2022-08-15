package me.adrianed.authmevelocity.common.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class PaperConfiguration {
    @Comment("Enable Debug Mode")
    private boolean debug = false;
    public boolean debug() {
        return this.debug;
    }
}
