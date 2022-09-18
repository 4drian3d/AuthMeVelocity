package me.adrianed.authmevelocity.velocity.objects;

import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.crypto.IdentifiedKey;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.player.PlayerSettings;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import com.velocitypowered.api.proxy.player.TabList;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.GameProfile.Property;
import com.velocitypowered.api.util.ModInfo;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;

public class TestPlayer implements Player {
    public static final TestPlayer SIGNED = new TestPlayer(
        true, Tristate.TRUE, ProtocolVersion.MINECRAFT_1_19_1);
    public static final TestPlayer UNSIGNED = new TestPlayer(
        false, Tristate.TRUE, ProtocolVersion.MINECRAFT_1_19_1);
    public static final TestPlayer OLD_SIGNED = new TestPlayer(
        true, Tristate.TRUE, ProtocolVersion.MINECRAFT_1_19);
    public static final TestPlayer OLD = new TestPlayer(
        false, Tristate.TRUE, ProtocolVersion.MINECRAFT_1_18_2);

    private Locale locale = Locale.getDefault();
    private final boolean signed;
    private final Tristate tristate;
    private final ProtocolVersion version;
    

    public TestPlayer(boolean signed, Tristate tristate, ProtocolVersion version) {
        this.signed = signed;
        this.tristate = tristate;
        this.version = version;
    }
    @Override
    public Tristate getPermissionValue(String permission) {
        return tristate;
    }

    @Override
    public @NotNull Identity identity() {
        return Identity.nil();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress("a", 404);
    }

    @Override
    public Optional<InetSocketAddress> getVirtualHost() {
        return Optional.empty();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return version;
    }

    @Override
    public IdentifiedKey getIdentifiedKey() {
        return signed ? new IdentifiedKey() {

            @Override
            public PublicKey getSigner() {
                return null;
            }

            @Override
            public Instant getExpiryTemporal() {
                return Instant.now();
            }

            @Override
            public @Nullable byte[] getSignature() {
                return null;
            }

            @Override
            public PublicKey getSignedPublicKey() {
                return null;
            }

            @Override
            public boolean verifyDataSignature(byte[] signature, byte[]... toVerify) {
                return false;
            }

            @Override
            public @Nullable UUID getSignatureHolder() {
                return null;
            }

            @Override
            public Revision getKeyRevision() {
                return null;
            }
        } : null;
    }

    @Override
    public String getUsername() {
        return "TestPlayer";
    }

    @Override
    public @Nullable Locale getEffectiveLocale() {
        return locale;
    }

    @Override
    public void setEffectiveLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public UUID getUniqueId() {
        return UUID.randomUUID();
    }

    private RegisteredServer server;

    @Override
    public Optional<ServerConnection> getCurrentServer() {
        return Optional.ofNullable(server)
            .map(sv -> new TestServerConnection(sv, this));
    }

    public TestPlayer setServer(RegisteredServer server) {
        this.server = server;
        return this;
    }

    @Override
    public PlayerSettings getPlayerSettings() {
        return null;
    }

    @Override
    public Optional<ModInfo> getModInfo() {
        return Optional.empty();
    }

    @Override
    public long getPing() {
        return 0;
    }

    @Override
    public boolean isOnlineMode() {
        return true;
    }

    @Override
    public ConnectionRequestBuilder createConnectionRequest(RegisteredServer server) {
        return null;
    }

    @Override
    public List<Property> getGameProfileProperties() {
        return Collections.emptyList();
    }

    @Override
    public void setGameProfileProperties(List<Property> properties) {
    }

    @Override
    public GameProfile getGameProfile() {
        return null;
    }

    @Override
    public void clearHeaderAndFooter() {
        
    }

    @Override
    public Component getPlayerListHeader() {
        return Component.empty();
    }

    @Override
    public Component getPlayerListFooter() {
        return Component.empty();
    }

    @Override
    public TabList getTabList() {
        return null;
    }

    @Override
    public void disconnect(Component reason) {        
    }

    @Override
    public void spoofChatInput(String input) {
    }

    @Override
    public void sendResourcePack(String url) {
    }

    @Override
    public void sendResourcePack(String url, byte[] hash) {
    }

    @Override
    public void sendResourcePackOffer(ResourcePackInfo packInfo) {
    }

    @Override
    public @Nullable ResourcePackInfo getAppliedResourcePack() {
        return null;
    }

    @Override
    public @Nullable ResourcePackInfo getPendingResourcePack() {
        return null;
    }

    @Override
    public boolean sendPluginMessage(ChannelIdentifier identifier, byte[] data) {
        return true;
    }

    @Override
    public @Nullable String getClientBrand() {
        return null;
    }
    
}
