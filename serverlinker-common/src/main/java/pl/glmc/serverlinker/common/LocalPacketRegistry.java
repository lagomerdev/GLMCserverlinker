package pl.glmc.serverlinker.common;

import pl.glmc.api.common.packet.PacketInfo;
import pl.glmc.serverlinker.common.other.*;
import pl.glmc.serverlinker.common.rtp.*;
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

    public static final class Teleport {
        public static final PacketInfo GET_RANDOM_COORDINATES_REQUEST = PacketInfo.make("serverlinker.get_random_coordinates_request", GetRandomCoordinatesRequest.class);
        public static final PacketInfo GET_RANDOM_COORDINATES_RESPONSE = PacketInfo.make("serverlinker.get_random_coordinates_response", GetRandomCoordinatesResponse.class);
        public static final PacketInfo TELEPORT_PLAYER_REQUEST = PacketInfo.make("serverlinker.teleport_player_request", TeleportPlayerRequest.class);
        public static final PacketInfo TELEPORT_PLAYER_RESPONSE = PacketInfo.make("serverlinker.teleport_player_response", TeleportPlayerResponse.class);
        public static final PacketInfo REVERSE_TELEPORT_PLAYER_REQUEST = PacketInfo.make("serverlinker.reverse_teleport_player_request", ReverseTeleportPlayerRequest.class);
        public static final PacketInfo REVERSE_TELEPORT_PLAYER_RESPONSE = PacketInfo.make("serverlinker.reverse_teleport_player_response", ReverseTeleportPlayerResponse.class);
    }
    
    public static final class Other {
        public static final PacketInfo GET_PLAYER_COORDINATES_REQUEST = PacketInfo.make("serverlinker.get_player_coordinates_request", GetPlayerCoordinatesRequest.class);
        public static final PacketInfo GET_PLAYER_COORDINATES_RESPONSE = PacketInfo.make("serverlinker.get_player_coordinates_response", GetPlayerCoordinatesResponse.class);
        public static final PacketInfo STUN_PLAYER_REQUEST = PacketInfo.make("serverlinker.stun_player_request", StunPlayerRequest.class);
        public static final PacketInfo STUN_PLAYER_RESPONSE = PacketInfo.make("serverlinker.stun_player_response", StunPlayerResponse.class);
        public static final PacketInfo STUN_PLAYER_CONFIRMATION = PacketInfo.make("serverlinker.stun_player_confirmation", StunPlayerConfirmation.class);
    }
}
