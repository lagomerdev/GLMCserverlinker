package pl.glmc.serverlinker.bungee.api.rtp;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.serverlinker.api.bungee.rtp.RandomTeleportService;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.api.common.TransferMetaData;
import pl.glmc.serverlinker.api.common.TransferMetaKey;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.bungee.api.rtp.packet.GetRandomCoordinatesHandler;
import pl.glmc.serverlinker.common.rtp.GetRandomCoordinatesRequest;
import pl.glmc.serverlinker.common.sector.SectorData;

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
    public CompletableFuture<TransferAPI.TransferResult> transferPlayerToRandomCoords(ProxiedPlayer player, String serverTarget, boolean force) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(serverTarget);
        CompletableFuture<TransferAPI.TransferResult> resposne = new CompletableFuture<>();

        this.plugin.getGlmcTransferProvider().getRandomTeleportService().getRandomCoords(serverTarget)
                .thenAcceptAsync(transferLocation -> {
                    if (transferLocation == null) {
                        resposne.complete(TransferAPI.TransferResult.FAILED_TRANSFERRING);
                    } else {
                        this.plugin.getProxy().getScheduler().schedule(this.plugin, () -> {
                            player.sendTitle(this.plugin.getProxy().createTitle()
                                    .title(new TextComponent(ChatColor.GOLD + "Losowa teleportacja!"))
                                    .subTitle(new TextComponent(ChatColor.YELLOW + serverTarget + ", X: " + transferLocation.getX() + ", Y: " + transferLocation.getY() + ", Z: " + transferLocation.getZ()))
                                    .fadeIn(20).stay(100).fadeOut(20));
                        }, 10, TimeUnit.MILLISECONDS);

                        TransferMetaData transferMetaData = new TransferMetaData();
                        transferMetaData.setItem(TransferMetaKey.TELEPORT_ON_COORDS, this.plugin.getGson().toJson(transferLocation));

                        resposne.complete(this.plugin.getGlmcTransferProvider().getTransferService().transferPlayer(player.getUniqueId(), serverTarget, transferMetaData, TransferAPI.TransferReason.SERVER_MATCH, false).join());
                    }
                });


        return resposne;
    }
}
