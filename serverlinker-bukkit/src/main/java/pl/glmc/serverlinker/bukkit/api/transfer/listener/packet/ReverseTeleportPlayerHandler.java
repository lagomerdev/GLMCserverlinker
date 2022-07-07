package pl.glmc.serverlinker.bukkit.api.transfer.listener.packet;

import pl.glmc.api.common.packet.listener.ResponseHandlerListener;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.rtp.ReverseTeleportPlayerResponse;
import pl.glmc.serverlinker.common.transfer.TransferResponse;

public class ReverseTeleportPlayerHandler extends ResponseHandlerListener<ReverseTeleportPlayerResponse, Boolean> {
    public ReverseTeleportPlayerHandler() {
        super(LocalPacketRegistry.Teleport.REVERSE_TELEPORT_PLAYER_RESPONSE, ReverseTeleportPlayerResponse.class);
    }

    @Override
    public void received(ReverseTeleportPlayerResponse teleportPlayerResponse) {
        this.complete(teleportPlayerResponse.getOriginUniqueId(), teleportPlayerResponse.isSuccess());
    }
}
