package pl.glmc.serverlinker.bungee.api.player.packet;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.rtp.ReverseTeleportPlayerRequest;
import pl.glmc.serverlinker.common.rtp.ReverseTeleportPlayerResponse;

public class ReverseTeleportPlayerListener extends PacketListener<ReverseTeleportPlayerRequest> {

    private final GlmcServerLinkerBungee plugin;

    public ReverseTeleportPlayerListener(GlmcServerLinkerBungee plugin) {
        super(LocalPacketRegistry.Teleport.REVERSE_TELEPORT_PLAYER_REQUEST, ReverseTeleportPlayerRequest.class);

        this.plugin = plugin;

        this.plugin.getGlmcApiBungee().getPacketService().registerListener(this, this.plugin);
    }

    @Override
    public void received(ReverseTeleportPlayerRequest request) {
        this.plugin.getGlmcTransferProvider().getTransferHelper().teleportPlayerToCoords(
                request.getPlayerUniqueId(),
                this.plugin.getGlmcTransferProvider().getSectorManager().getSectorTypes().get(request.getSectorType()),
                request.getTransferLocation(),
                request.isForce())
                    .thenAccept(success -> {
                        ReverseTeleportPlayerResponse respone = new ReverseTeleportPlayerResponse(success, request.getUniqueId());
                        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(respone, request.getSender());
                    });
    }
}
