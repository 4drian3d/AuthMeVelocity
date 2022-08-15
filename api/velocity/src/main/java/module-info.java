/**AuthMeVelocity Velocity API Module */
@SuppressWarnings({"requires-automatic", "requires-transitive-automatic"})
module me.adrianed.authmevelocity.api.velocity {
    requires transitive com.velocitypowered.api;
    requires static org.jetbrains.annotations;
    exports me.adrianed.authmevelocity.api.velocity;
    exports me.adrianed.authmevelocity.api.velocity.event;
}
