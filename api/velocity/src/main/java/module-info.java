/**AuthMeVelocity Velocity API Module */
@SuppressWarnings({"requires-automatic", "requires-transitive-automatic"})
module io.github._4drian3d.authmevelocity.api.velocity {
  requires transitive com.velocitypowered.api;
  requires static org.jetbrains.annotations;
  exports io.github._4drian3d.authmevelocity.api.velocity;
  exports io.github._4drian3d.authmevelocity.api.velocity.event;
  exports io.github._4drian3d.authmevelocity.api.velocity.event.authserver;
}
