package pl.glmc.serverlinker.api.bungee.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.glmc.serverlinker.api.common.player.PlayerLocation;
import pl.glmc.serverlinker.api.common.player.StunResult;

import java.util.concurrent.CompletableFuture;

public interface PlayerService {

    /**
     *
     * @param player
     * @return
     */
    public CompletableFuture<PlayerLocation> getPlayerLocation(ProxiedPlayer player);

    /**
     *
     * @param player
     * @param seconds
     * @return
     */
    public CompletableFuture<StunResult> stunPlayer(ProxiedPlayer player, int seconds);
}
