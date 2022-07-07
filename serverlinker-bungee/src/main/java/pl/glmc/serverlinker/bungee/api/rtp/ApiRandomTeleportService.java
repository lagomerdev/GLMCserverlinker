package pl.glmc.serverlinker.bungee.api.rtp;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.serverlinker.api.bungee.rtp.RandomTeleportService;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.bungee.api.rtp.packet.GetRandomCoordinatesHandler;
import pl.glmc.serverlinker.common.rtp.GetRandomCoordinatesRequest;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ApiRandomTeleportService implements RandomTeleportService {
    private final GlmcServerLinkerBungee plugin;

    private final GetRandomCoordinatesHandler getRandomCoordinatesHandler;

    public ApiRandomTeleportService(GlmcServerLinkerBungee plugin) {
        this.plugin = plugin;

        this.getRandomCoordinatesHandler = new GetRandomCoordinatesHandler();
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(getRandomCoordinatesHandler, this.plugin);
    }

    @Override
    public CompletableFuture<TransferLocation> getRandomCoords(String serverTarget) {
        var randomCoordinatesRequest = new GetRandomCoordinatesRequest();

        var request = this.getRandomCoordinatesHandler.create(randomCoordinatesRequest.getUniqueId());

        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(randomCoordinatesRequest, serverTarget);

        return request;
    }

    @Override
    public CompletableFuture<Boolean> teleportPlayerToRandomCoords(ProxiedPlayer player, String serverTarget, boolean force) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(serverTarget);
        CompletableFuture<Boolean> resposne = new CompletableFuture<>();

        this.plugin.getGlmcTransferProvider().getRandomTeleportService().getRandomCoords(serverTarget)
                .thenAcceptAsync(transferLocation -> {
                    if (transferLocation == null) {
                        resposne.complete(false);
                    } else {
                        this.plugin.getProxy().getScheduler().schedule(this.plugin, () -> {
                            player.sendTitle(this.plugin.getProxy().createTitle()
                                    .title(new TextComponent(ChatColor.GOLD + "Losowa teleportacja!"))
                                    .subTitle(new TextComponent(ChatColor.YELLOW + serverTarget + ", X: " + transferLocation.getX() + ", Y: " + transferLocation.getY() + ", Z: " + transferLocation.getZ()))
                                    .fadeIn(20).stay(100).fadeOut(20));
                        }, 350, TimeUnit.MILLISECONDS);

                        var sectorData = this.plugin.getGlmcTransferProvider().getSectorManager().getSectors().get(serverTarget);

                        resposne.complete(this.plugin.getGlmcTransferProvider().getTransferHelper()
                                .teleportPlayerToCoords(player.getUniqueId(), sectorData, transferLocation, false).join());
                    }
                });


        return resposne;
    }
}
