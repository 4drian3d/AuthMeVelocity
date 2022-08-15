/**AuthMeVelocity Paper API Module */
@SuppressWarnings({"requires-automatic", "requires-transitive-automatic"})
module me.adrianed.authmevelocity.api.paper {
    requires transitive org.bukkit;
    requires static org.jetbrains.annotations;
    exports me.adrianed.authmevelocity.api.paper.event;
}
