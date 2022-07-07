package pl.glmc.serverlinker.bukkit.api.player.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.glmc.serverlinker.api.common.player.StunResult;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.player.ApiPlayerService;

public class PlayerStunListener implements Listener {

    private final GlmcServerLinkerBukkit plugin;
    private final ApiPlayerService playerService;

    public PlayerStunListener(GlmcServerLinkerBukkit plugin, ApiPlayerService playerService) {
        this.plugin = plugin;
        this.playerService = playerService;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent moveEvent) {
        if (moveEvent.getFrom().getBlockX() == moveEvent.getTo().getBlockX()
                && moveEvent.getFrom().getBlockZ() == moveEvent.getTo().getBlockZ()
                && moveEvent.getFrom().getBlockY() == moveEvent.getTo().getBlockY()) {
            return;
        }

        var player = moveEvent.getPlayer();
        if (this.playerService.isStunned(player)) {
            this.playerService.sendStunResult(player, StunResult.MOVED);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent quitEvent) {
        if (this.playerService.isStunned(quitEvent.getPlayer())) {
            this.playerService.sendStunResult(quitEvent.getPlayer(), StunResult.PLAYER_QUIT);
        }
    }
}
