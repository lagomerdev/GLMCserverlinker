package pl.glmc.serverlinker.bukkit.api.transfer.listener.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.transfer.ApiTransferService;

public class PlayerJoinQuitListener implements Listener {
    private final GlmcServerLinkerBukkit plugin;
    private final ApiTransferService transferService;

    public PlayerJoinQuitListener(GlmcServerLinkerBukkit plugin, ApiTransferService transferService) {
        this.plugin = plugin;
        this.transferService = transferService;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onPreLoginEvent(AsyncPlayerPreLoginEvent preLoginEvent) {
        if (this.transferService.isAllowed(preLoginEvent.getUniqueId())) {
            this.transferService.transferCompleted(preLoginEvent.getUniqueId());
        } else {
            //preLoginEvent.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "[Transfer] Possible data synchronization violation detected - joining cancelled...");
        }
    }

    @EventHandler
    public void onPostJoin(PlayerJoinEvent joinEvent) {
        this.transferService.processJoin(joinEvent.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent quitEvent) {
        this.transferService.processDisconnect(quitEvent.getPlayer());
    }
}
