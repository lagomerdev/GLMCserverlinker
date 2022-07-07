package pl.glmc.serverlinker.bungee.api.transfer.listener.event;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.bungee.api.player.PlayerManager;

import java.util.concurrent.CompletableFuture;

public class PlayerJoinQuitListener implements Listener {

    private final GlmcServerLinkerBungee plugin;
    private final PlayerManager playerManager;

    public PlayerJoinQuitListener(GlmcServerLinkerBungee plugin, PlayerManager playerManager) {
        this.plugin = plugin;
        this.playerManager = playerManager;

        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, this);
    }

    @EventHandler
    public void onLogin(ServerConnectedEvent connectedEvent) {
         if (!connectedEvent.getServer().getInfo().getName().equals("afterburner")) {
             return;
         }

        long start = System.currentTimeMillis();
        CompletableFuture<TransferAPI.JoinResult> completableFuture = this.playerManager.processJoin(connectedEvent.getPlayer());
        completableFuture.thenAccept(joinResult -> {
            long end = System.currentTimeMillis();
            this.plugin.getLogger().info("Joining " + connectedEvent.getPlayer().getName() + " result: " + joinResult);
            this.plugin.getLogger().info("Time taken: " + (end-start) + "ms");
        });
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent disconnectEvent) {
        this.playerManager.processDisconnect(disconnectEvent.getPlayer());
    }
}
