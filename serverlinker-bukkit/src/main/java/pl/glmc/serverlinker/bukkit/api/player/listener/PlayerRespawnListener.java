package pl.glmc.serverlinker.bukkit.api.player.listener;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import pl.glmc.serverlinker.api.common.TransferLocation;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;

import java.time.Duration;

public class PlayerRespawnListener implements Listener {

    private final GlmcServerLinkerBukkit plugin;

    public PlayerRespawnListener(GlmcServerLinkerBukkit plugin) {
        this.plugin = plugin;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent deathEvent) {
        deathEvent.setKeepInventory(false);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onRespawn(PlayerRespawnEvent respawnEvent) {
        respawnEvent.setRespawnLocation(new Location(this.plugin.getGlmcTransferProvider().getSectorManager().getWorld(), 0, 0 ,0));

        var player = respawnEvent.getPlayer();

        player.sendTitlePart(TitlePart.TITLE, Component.text(ChatColor.GREEN + "Trwa odradzanie..."));
        player.sendTitlePart(TitlePart.SUBTITLE, Component.text(ChatColor.GREEN + " "));
        player.sendTitlePart(TitlePart.TIMES, Title.Times.of(Duration.ofMillis(200), Duration.ofSeconds(2), Duration.ofMillis(200)));

        this.plugin.getGlmcTransferProvider().getTransferHelper().teleportPlayerToCoords(player.getUniqueId(), "spawn", new TransferLocation(-151.5, 134, -221.5), true)
                .thenAccept(System.out::println);
    }

    @EventHandler
    public void onPostRespawn(PlayerPostRespawnEvent postRespawnEvent) {
        //this.plugin.getGlmcTransferProvider().getTransferHelper().teleportPlayerToCoords(postRespawnEvent.getPlayer().getUniqueId(), "spawn", new TransferLocation(-151.5, 134, -221.5), true)
        //        .thenAccept(System.out::println);
    }
}
