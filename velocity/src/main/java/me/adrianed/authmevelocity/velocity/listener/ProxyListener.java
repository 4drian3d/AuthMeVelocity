package me.adrianed.authmevelocity.velocity.listener;

import me.adrianed.authmevelocity.velocity.utils.AuthmeUtils;
import me.adrianed.authmevelocity.velocity.AuthMeVelocityPlugin;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.minimessage.MiniMessage;

public final class ProxyListener {
    private final AuthMeVelocityPlugin plugin;

    public ProxyListener(AuthMeVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public EventTask onDisconnect(final DisconnectEvent event) {
        return EventTask.async(() -> plugin.removePlayer(event.getPlayer()));
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onCommandExecute(final CommandExecuteEvent event, Continuation continuation) {
        if (!(event.getCommandSource() instanceof Player)){
            continuation.resume();
            return;
        }

        Player player = (Player)event.getCommandSource();

        if (plugin.isLogged(player)) {
            continuation.resume();
            return;
        }

        if (plugin.isInAuthServer(player)) {
            String command = AuthmeUtils.getFirstArgument(event.getCommand());
            if (!plugin.config().get().commands().allowedCommands().contains(command)) {
                sendBlockedMessage(player);
                event.setResult(CommandExecuteEvent.CommandResult.denied());
            }
        } else {
            sendBlockedMessage(player);
            event.setResult(CommandExecuteEvent.CommandResult.denied());
        }
        continuation.resume();
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerChat(final PlayerChatEvent event) {
        if (plugin.isNotLogged(event.getPlayer())) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onTabComplete(TabCompleteEvent event){
        if (plugin.isLogged(event.getPlayer())) {
            return;
        }

        final String command = event.getPartialMessage();
        for (final String allowed : plugin.config().get().commands().allowedCommands()) {
            if (allowed.startsWith(command)) {
                return;
            }
        }

        event.getSuggestions().clear();
    }

    void sendBlockedMessage(Player player){
        String blockedMessage = plugin.config().get().commands().blockedCommandMessage();
        if (!blockedMessage.isBlank()){
            player.sendMessage(MiniMessage.miniMessage().deserialize(blockedMessage));
        }
    }
    
}
