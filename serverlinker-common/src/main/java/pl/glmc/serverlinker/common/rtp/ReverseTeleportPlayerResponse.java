package pl.glmc.serverlinker.common.rtp;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class ReverseTeleportPlayerResponse extends ResponsePacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Teleport.REVERSE_TELEPORT_PLAYER_RESPONSE;

    public ReverseTeleportPlayerResponse(boolean success, UUID originUniqueId) {
        super(success, originUniqueId);
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
