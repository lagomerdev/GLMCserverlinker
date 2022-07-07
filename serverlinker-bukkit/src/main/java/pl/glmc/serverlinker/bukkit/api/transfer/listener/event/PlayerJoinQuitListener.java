package pl.glmc.serverlinker.bukkit.api.transfer.listener.event;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.glmc.serverlinker.api.bukkit.transfer.TransferProcessEvent;
import pl.glmc.serverlinker.api.common.TransferMetaData;
import pl.glmc.serverlinker.api.common.TransferMetaKey;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.transfer.ApiTransferService;

import java.time.Duration;
import java.util.UUID;

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
        UUID playerUniqueId = preLoginEvent.getUniqueId();
        if (this.transferService.isTransferred(playerUniqueId)) {
            //
        } else {
            preLoginEvent.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "[Transfer] Possible data synchronization violation detected - joining cancelled...");
        }
    }

    @EventHandler
    public void onPostJoin(PlayerJoinEvent joinEvent) {
        var player = joinEvent.getPlayer();
        if (this.transferService.isTransferred(player.getUniqueId())) {
            this.transferService.transferCompleted(player.getUniqueId());
        }

        this.transferService.processJoin(player);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent quitEvent) {
        this.transferService.processDisconnect(quitEvent.getPlayer());
    }
}
