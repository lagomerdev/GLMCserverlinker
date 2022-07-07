package pl.glmc.serverlinker.common.other;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class StunPlayerRequest extends RequestPacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Other.STUN_PLAYER_REQUEST;

    private final UUID playerUniqueId;
    private final int seconds;

    public StunPlayerRequest(UUID playerUniqueId, int seconds) {
        this.playerUniqueId = playerUniqueId;
        this.seconds = seconds;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public int getSeconds() {
        return seconds;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
