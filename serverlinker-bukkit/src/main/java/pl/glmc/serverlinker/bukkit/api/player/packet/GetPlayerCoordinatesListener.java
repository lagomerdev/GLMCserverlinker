package pl.glmc.serverlinker.bukkit.api.player.packet;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.player.ApiPlayerService;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.other.GetPlayerCoordinatesRequest;
import pl.glmc.serverlinker.common.other.GetPlayerCoordinatesResponse;

public class GetPlayerCoordinatesListener extends PacketListener<GetPlayerCoordinatesRequest> {

    private final GlmcServerLinkerBukkit plugin;
    private final ApiPlayerService playerService;

    public GetPlayerCoordinatesListener(GlmcServerLinkerBukkit plugin, ApiPlayerService playerService) {
        super(LocalPacketRegistry.Other.GET_PLAYER_COORDINATES_REQUEST, GetPlayerCoordinatesRequest.class);

        this.plugin = plugin;
        this.playerService = playerService;

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this, this.plugin);
    }

    @Override
    public void received(GetPlayerCoordinatesRequest getPlayerCoordinatesRequest) {
        var playerLocation = this.playerService.getPlayerLocation(getPlayerCoordinatesRequest.getPlayerUniqueId());

        GetPlayerCoordinatesResponse response = new GetPlayerCoordinatesResponse(
                playerLocation.isPresent(),
                getPlayerCoordinatesRequest.getUniqueId(),
                playerLocation);

        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(response, getPlayerCoordinatesRequest.getSender());
    }
}