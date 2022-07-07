package pl.glmc.serverlinker.common.other;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.RequestPacket;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class StunPlayerResponse extends ResponsePacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Other.STUN_PLAYER_RESPONSE;

    public StunPlayerResponse(boolean success, UUID originUniqueId) {
        super(success, originUniqueId);
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
