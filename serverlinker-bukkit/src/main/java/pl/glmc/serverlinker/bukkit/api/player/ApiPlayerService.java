package pl.glmc.serverlinker.bukkit.api.player;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import pl.glmc.serverlinker.api.common.player.PlayerLocation;
import pl.glmc.serverlinker.api.common.player.StunResult;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.player.listener.PlayerRespawnListener;
import pl.glmc.serverlinker.bukkit.api.player.listener.PlayerStunListener;
import pl.glmc.serverlinker.bukkit.api.player.packet.GetPlayerCoordinatesListener;
import pl.glmc.serverlinker.bukkit.api.player.packet.StunPlayerListener;
import pl.glmc.serverlinker.common.other.StunPlayerConfirmation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ApiPlayerService {

    private final GlmcServerLinkerBukkit plugin;

    private final HashMap<UUID, Integer> stunnedPlayers = new HashMap<>();

    public ApiPlayerService(GlmcServerLinkerBukkit plugin) {
        this.plugin = plugin;

        GetPlayerCoordinatesListener getPlayerCoordinatesListener = new GetPlayerCoordinatesListener(this.plugin, this);
        StunPlayerListener stunPlayerListener = new StunPlayerListener(this.plugin, this);

        PlayerStunListener playerStunListener = new PlayerStunListener(this.plugin, this);
        PlayerRespawnListener playerRespawnListener = new PlayerRespawnListener(this.plugin);
    }

    public PlayerLocation getPlayerLocation(UUID playerUniqueId) {
        var player = this.plugin.getServer().getPlayer(playerUniqueId);

        if (player == null || !player.isOnline()) {
            return new PlayerLocation();
        } else {
            var location = player.getLocation();

            return new PlayerLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }
    }

    public void stunPlayer(Player player, int seconds) {
        if (this.stunnedPlayers.containsKey(player.getUniqueId()))
            throw new IllegalArgumentException("Already stunned!");

        int taskId = this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            if (this.stunnedPlayers.containsKey(player.getUniqueId())) {
                if (player.isOnline()) this.sendStunResult(player, StunResult.SUCCESS);
                else this.sendStunResult(player, StunResult.PLAYER_OFFLINE);
            }
        }, seconds * 20L).getTaskId();

        this.stunnedPlayers.put(player.getUniqueId(), taskId);
    }

    public void sendStunResult(Player player, StunResult result) {
        var taskId = this.stunnedPlayers.remove(player.getUniqueId());
        if (taskId != null) this.plugin.getServer().getScheduler().cancelTask(taskId);

        StunPlayerConfirmation confirmation = new StunPlayerConfirmation(player.getUniqueId(), result);
        this.plugin.getGlmcApiBukkit().getPacketService().sendPacket(confirmation, "proxy");
    }

    public boolean isStunned(Player player) {
        return this.stunnedPlayers.containsKey(player.getUniqueId());
    }
}
