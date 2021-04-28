package pl.glmc.serverlinker.common.transfer;

import pl.glmc.api.common.packet.InfoPacket;
import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.serverlinker.common.LocalPacketRegistry;

import java.util.UUID;

public class TransferData extends InfoPacket {
    private static final PacketInfo PACKET_INFO = LocalPacketRegistry.Transfer.TRANSFER_DATA;

    private final UUID playerUniqueId, originPacketUniqueId;
    private final String playerData, optionalData, originSender;

    public TransferData(UUID playerUniqueId, UUID originPacketUniqueId, String playerData, String optionalData, String originSender) {
        this.playerUniqueId = playerUniqueId;
        this.originPacketUniqueId = originPacketUniqueId;
        this.playerData = playerData;
        this.optionalData = optionalData;
        this.originSender = originSender;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public UUID getOriginPacketUniqueId() {
        return originPacketUniqueId;
    }

    public String getPlayerData() {
        return playerData;
    }

    public String getOptionalData() {
        return optionalData;
    }

    public String getOriginSender() {
        return originSender;
    }

    @Override
    public String getPacketId() {
        return PACKET_INFO.getId();
    }
}
