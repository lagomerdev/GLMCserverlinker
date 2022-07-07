package pl.glmc.serverlinker.bukkit.api.rtp.packet;

import org.bukkit.Location;
import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.rtp.TeleportPlayerRequest;
import pl.glmc.serverlinker.common.rtp.TeleportPlayerResponse;

public class TeleportPlayerListener extends PacketListener<TeleportPlayerRequest> {

    private final GlmcServerLinkerBukkit plugin;

    public TeleportPlayerListener(GlmcServerLinkerBukkit plugin) {
        super(LocalPacketRegistry.Teleport.TELEPORT_PLAYER_REQUEST, TeleportPlayerRequest.class);

        this.plugin = plugin;

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this, this.plugin);
    }

    @Override
    public void received(TeleportPlayerRequest teleportPlayerRequest) {
        var player = this.plugin.getServer().getPlayer(teleportPlayerRequest.getPlayerUniqueId());

        if (player == null || !player.isOnline()) {
            var teleportPlayerResponse = new TeleportPlayerResponse(false, teleportPlayerRequest.getUniqueId());
            this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(teleportPlayerResponse, teleportPlayerRequest.getSender());
        } else {
            if (teleportPlayerRequest.isToCoords()) {
                var transferLocation = teleportPlayerRequest.getTransferLocation();
                var location = new Location(
                        this.plugin.getGlmcTransferProvider().getSectorManager().getWorld(),
                        transferLocation.getX(),
                        transferLocation.getY(),
                        transferLocation.getZ()
                );

                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> player.teleportAsync(location)
                        .thenAccept(success -> {
                            var teleportPlayerResponse = new TeleportPlayerResponse(success, teleportPlayerRequest.getUniqueId());
                            this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(teleportPlayerResponse, teleportPlayerRequest.getSender());
                        })
                );
            } else {
                var playerTarget = this.plugin.getServer().getPlayer(teleportPlayerRequest.getPlayerTargetUniqueId());

                if (playerTarget == null || !playerTarget.isOnline()) {
                    var teleportPlayerResponse = new TeleportPlayerResponse(false, teleportPlayerRequest.getUniqueId());
                    this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(teleportPlayerResponse, teleportPlayerRequest.getSender());

                    return;
                }

                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> player.teleportAsync(playerTarget.getLocation())
                        .thenAccept(success -> {
                            var teleportPlayerResponse = new TeleportPlayerResponse(success, teleportPlayerRequest.getUniqueId());
                            this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(teleportPlayerResponse, teleportPlayerRequest.getSender());
                        })
                );
            }
        }
    }
}
