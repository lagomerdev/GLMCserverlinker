package pl.glmc.serverlinker.common;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.serverlinker.common.transfer.*;

public class LocalPacketRegistry {

    public static final class Transfer {
        public static final PacketInfo OFFLINE_DATA_REQUEST = PacketInfo.make("serverlinker.offline_data_request", OfflineDataRequest.class);
        public static final PacketInfo OFFLINE_DATA_RESPONSE = PacketInfo.make("serverlinker.offline_data_response", OfflineDataResponse.class);
        public static final PacketInfo TRANSFER_REQUEST = PacketInfo.make("serverlinker.transfer_request", TransferRequest.class);
        public static final PacketInfo TRANSFER_RESPONSE = PacketInfo.make("serverlinker.transfer_response", TransferResponse.class);
        public static final PacketInfo TRANSFER_APPROVAL_REQUEST = PacketInfo.make("serverlinker.transfer_approval_request", TransferApprovalRequest.class);
        public static final PacketInfo TRANSFER_APPROVAL_RESPONSE = PacketInfo.make("serverlinker.transfer_approval_response", TransferApprovalResponse.class);
        public static final PacketInfo TRANSFER_DATA = PacketInfo.make("serverlinker.transfer_data", TransferData.class);
        public static final PacketInfo TRANSFER_DATA_REQUEST = PacketInfo.make("serverlinker.transfer_data_request", TransferDataRequest.class);
        public static final PacketInfo TRANSFER_DATA_RESPONSE = PacketInfo.make("serverlinker.transfer_data_response", TransferDataResponse.class);
        public static final PacketInfo TRANSFER_UNFREEZE = PacketInfo.make("serverlinker.transfer_unfreeze", TransferUnfreeze.class);
    }
}
