package pl.glmc.serverlinker.common.other;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.api.common.packet.ResponsePacket;
import pl.glmc.serverlinker.api.common.player.StunResult;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class StunPlayerConfirmation extends ResponsePacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Other.STUN_PLAYER_CONFIRMATION;

    private final StunResult stunResult;

    public StunPlayerConfirmation(UUID originUniqueId, StunResult stunResult) {
        super(stunResult == StunResult.SUCCESS, originUniqueId);

        this.stunResult = stunResult;
    }

    public StunResult getStunResult() {
        return stunResult;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
