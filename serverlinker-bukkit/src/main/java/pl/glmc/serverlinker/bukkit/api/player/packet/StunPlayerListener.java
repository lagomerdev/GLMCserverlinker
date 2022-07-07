package pl.glmc.serverlinker.bukkit.api.player.packet;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.player.ApiPlayerService;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.other.GetPlayerCoordinatesRequest;
import pl.glmc.serverlinker.common.other.GetPlayerCoordinatesResponse;
import pl.glmc.serverlinker.common.other.StunPlayerRequest;
import pl.glmc.serverlinker.common.other.StunPlayerResponse;

public class StunPlayerListener extends PacketListener<StunPlayerRequest> {

    private final GlmcServerLinkerBukkit plugin;
    private final ApiPlayerService playerService;

    public StunPlayerListener(GlmcServerLinkerBukkit plugin, ApiPlayerService playerService) {
        super(LocalPacketRegistry.Other.STUN_PLAYER_REQUEST, StunPlayerRequest.class);

        this.plugin = plugin;
        this.playerService = playerService;

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this, this.plugin);
    }

    @Override
    public void received(StunPlayerRequest stunPlayerRequest) {
        var player = this.plugin.getServer().getPlayer(stunPlayerRequest.getPlayerUniqueId());
        var stunned = player != null && player.isOnline();
        if (stunned) this.playerService.stunPlayer(player, stunPlayerRequest.getSeconds());

        var response = new StunPlayerResponse(stunned, stunPlayerRequest.getUniqueId());
        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(response, stunPlayerRequest.getSender());
    }
}