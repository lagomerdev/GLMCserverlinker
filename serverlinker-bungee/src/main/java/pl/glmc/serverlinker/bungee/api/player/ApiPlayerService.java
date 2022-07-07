package pl.glmc.serverlinker.bungee.api.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.serverlinker.api.bungee.player.PlayerService;
import pl.glmc.serverlinker.api.common.player.StunResult;
import pl.glmc.serverlinker.api.common.player.PlayerLocation;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.bungee.api.player.packet.GetPlayerCoordinatesHandler;
import pl.glmc.serverlinker.bungee.api.player.packet.ReverseTeleportPlayerListener;
import pl.glmc.serverlinker.bungee.api.player.packet.StunPlayerConfirmationHandler;
import pl.glmc.serverlinker.bungee.api.player.packet.StunPlayerHandler;
import pl.glmc.serverlinker.common.other.GetPlayerCoordinatesRequest;
import pl.glmc.serverlinker.common.other.StunPlayerRequest;
import pl.glmc.serverlinker.common.rtp.ReverseTeleportPlayerRequest;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ApiPlayerService implements PlayerService {

    private final GlmcServerLinkerBungee plugin;

    private GetPlayerCoordinatesHandler getPlayerCoordinatesHandler;
    private StunPlayerHandler stunPlayerHandler;
    private StunPlayerConfirmationHandler stunPlayerConfirmationHandler;

    private final Set<UUID> stunned = new HashSet<>();

    public ApiPlayerService(GlmcServerLinkerBungee plugin) {
        this.plugin = plugin;

        this.getPlayerCoordinatesHandler = new GetPlayerCoordinatesHandler();
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(getPlayerCoordinatesHandler, this.plugin);

        this.stunPlayerHandler = new StunPlayerHandler();
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(stunPlayerHandler, this.plugin);

        this.stunPlayerConfirmationHandler = new StunPlayerConfirmationHandler();
        this.plugin.getGlmcApiBungee().getPacketService().registerListener(stunPlayerConfirmationHandler, this.plugin);

        ReverseTeleportPlayerListener reverseTeleportPlayerListener = new ReverseTeleportPlayerListener(this.plugin);
    }

    @Override
    public CompletableFuture<PlayerLocation> getPlayerLocation(ProxiedPlayer player) {
        Objects.requireNonNull(player);
        var server = player.getServer().getInfo().getName();

        if (!this.plugin.getGlmcTransferProvider().getSectorManager().isServerSector(server))
            CompletableFuture.failedFuture(new IllegalArgumentException("Server is not a sector")) ;

        GetPlayerCoordinatesRequest request = new GetPlayerCoordinatesRequest(player.getUniqueId());

        var result = this.getPlayerCoordinatesHandler.create(request.getUniqueId())
                .completeOnTimeout(new PlayerLocation(), 10, TimeUnit.SECONDS);

        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(request, server);

        return result;
    }

    @Override
    public CompletableFuture<StunResult> stunPlayer(ProxiedPlayer player, int seconds) {
        Objects.requireNonNull(player);
        if (this.stunned.contains(player.getUniqueId()))
            return CompletableFuture.completedFuture(StunResult.ALREADY_STUNED);
        stunned.add(player.getUniqueId());

        var server = player.getServer().getInfo().getName();

        if (!this.plugin.getGlmcTransferProvider().getSectorManager().isServerSector(server))
            CompletableFuture.failedFuture(new IllegalArgumentException("Server is not a sector")) ;

        var result = new CompletableFuture<StunResult>();

        StunPlayerRequest request = new StunPlayerRequest(player.getUniqueId(), seconds);
        this.stunPlayerHandler.create(request.getUniqueId())
                .completeOnTimeout(false, 3, TimeUnit.SECONDS)
                .thenAccept(added -> {
                    if (!added) result.complete(StunResult.INIT_FAILED);
                    else this.stunPlayerConfirmationHandler.create(player.getUniqueId())
                            .completeOnTimeout(StunResult.TIMEOUT, seconds + 3, TimeUnit.SECONDS)
                            .thenAccept(success -> {
                                if (!player.isConnected()) result.complete(StunResult.PLAYER_OFFLINE);
                                else result.complete(success);
                            });
                });

        this.plugin.getGlmcApiBungee().getPacketService().sendPacket(request, server);

        result.thenAccept(test -> this.stunned.remove(player.getUniqueId()));

        return result;
    }
}
