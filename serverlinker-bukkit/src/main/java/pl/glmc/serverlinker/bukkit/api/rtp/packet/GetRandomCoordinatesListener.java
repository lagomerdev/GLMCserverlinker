package pl.glmc.serverlinker.bukkit.api.rtp.packet;

import pl.glmc.api.common.packet.listener.PacketListener;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.rtp.ApiRandomTeleportService;
import pl.glmc.serverlinker.common.LocalPacketRegistry;
import pl.glmc.serverlinker.common.rtp.GetRandomCoordinatesRequest;
import pl.glmc.serverlinker.common.rtp.GetRandomCoordinatesResponse;

public class GetRandomCoordinatesListener extends PacketListener<GetRandomCoordinatesRequest> {

    private final GlmcServerLinkerBukkit plugin;
    private final ApiRandomTeleportService randomTeleportService;

    public GetRandomCoordinatesListener(GlmcServerLinkerBukkit plugin, ApiRandomTeleportService randomTeleportService) {
        super(LocalPacketRegistry.Teleport.GET_RANDOM_COORDINATES_REQUEST, GetRandomCoordinatesRequest.class);

        this.plugin = plugin;
        this.randomTeleportService = randomTeleportService;

        this.plugin.getGlmcApiBukkit().getPacketService().registerListener(this, this.plugin);
    }

    @Override
    public void received(GetRandomCoordinatesRequest getRandomCoordinatesRequest) {
        this.randomTeleportService.getRandomCoords()
                .thenAcceptAsync(transferLocation -> {
                    int tries = 1;
                    while (transferLocation == null && tries < 10) {
                        transferLocation = this.randomTeleportService.getRandomCoords().join();

                        tries++;
                    }

                    this.plugin.getLogger().warning("Found: " + (transferLocation == null) + ", " + tries + " tries");

                    var getRandomCoordinatesResponse = new GetRandomCoordinatesResponse(
                            transferLocation == null,
                            getRandomCoordinatesRequest.getUniqueId(),
                            transferLocation
                    );

                    this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(
                            getRandomCoordinatesResponse,
                            getRandomCoordinatesRequest.getSender()
                    );
        });
    }
}