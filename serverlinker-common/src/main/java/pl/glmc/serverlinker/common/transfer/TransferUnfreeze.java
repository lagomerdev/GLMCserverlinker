package pl.glmc.serverlinker.common.transfer;

import pl.glmc.api.common.packet.InfoPacket;
import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class TransferUnfreeze extends InfoPacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Transfer.TRANSFER_UNFREEZE;

    private final UUID playerUniqueId;

    public TransferUnfreeze(UUID playerUniqueId) {
        this.playerUniqueId = playerUniqueId;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
