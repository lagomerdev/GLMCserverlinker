package pl.glmc.serverlinker.bungee.api.transfer.listener.bungee;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;
import pl.glmc.serverlinker.bungee.api.transfer.ApiTransferService;

public class PlayerJoinQuitListener implements Listener {

    private final GlmcServerLinkerBungee plugin;
    private final ApiTransferService transferService;

    public PlayerJoinQuitListener(GlmcServerLinkerBungee plugin, ApiTransferService transferService) {
        this.plugin = plugin;
        this.transferService = transferService;

        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, this);
    }

    @EventHandler
    public void onLogin(PostLoginEvent postLoginEvent) {
        this.transferService.processJoin(postLoginEvent.getPlayer());
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent disconnectEvent) {
        this.transferService.processDisconnect(disconnectEvent.getPlayer());
    }
}
