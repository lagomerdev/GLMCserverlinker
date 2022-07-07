package pl.glmc.serverlinker.bukkit.api.transfer.listener.event;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferMetaData;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.sector.ApiSectorManager;
import pl.glmc.serverlinker.api.common.sector.SectorData;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PlayerBorderListener implements Listener {
    private final GlmcServerLinkerBukkit plugin;
    private final HashMap<UUID, BossBar> distanceBossBars = new HashMap<>();
    
    private final SectorData sectorData;

    public PlayerBorderListener(GlmcServerLinkerBukkit plugin) {
        this.plugin = plugin;
        this.sectorData = ApiSectorManager.sectorData;
        
        this.setWorldBorder();

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onEntityMove(VehicleMoveEvent moveEvent) {
        if (moveEvent.getFrom().getBlockX() == moveEvent.getTo().getBlockX()
                && moveEvent.getFrom().getBlockZ() == moveEvent.getTo().getBlockZ()
                && moveEvent.getFrom().getBlockY() == moveEvent.getTo().getBlockY()) {
            return;
        }

        this.plugin.getServer().broadcastMessage(moveEvent.getVehicle().getType().toString());

        if (moveEvent.getVehicle().getPassengers().size() == 1
                && moveEvent.getVehicle().getPassengers().get(0) instanceof Player player) {
            var distanceInfo = this.distance(moveEvent.getTo());

            if (distanceInfo.getDistance() <= 30) {
                this.sendBorderInfo(player, distanceInfo);
            } else if (this.distanceBossBars.containsKey(player.getUniqueId())) {
                this.distanceBossBars.remove(player.getUniqueId()).removeAll();
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent moveEvent) {
        if (moveEvent.getFrom().getBlockX() == moveEvent.getTo().getBlockX()
                && moveEvent.getFrom().getBlockZ() == moveEvent.getTo().getBlockZ()
                && moveEvent.getFrom().getBlockY() == moveEvent.getTo().getBlockY()) {
            return;
        }

        var player = moveEvent.getPlayer();
        var distanceInfo = this.distance(moveEvent.getTo());

        if (distanceInfo.getDistance() <= 30) {
            this.sendBorderInfo(player, distanceInfo);
        } else if (this.distanceBossBars.containsKey(player.getUniqueId())) {
            this.distanceBossBars.remove(player.getUniqueId()).removeAll();
        }
    }

    private void setWorldBorder() {
        double x = (this.sectorData.getMaxX() + this.sectorData.getMinX())/2 + 0.5;
        double z = (this.sectorData.getMaxZ() + this.sectorData.getMinZ())/2 + 0.5;

        double size =  Math.abs(Math.max(this.sectorData.getMinX(), this.sectorData.getMaxX()) - Math.min(this.sectorData.getMinX(), this.sectorData.getMaxX())) + 2;
        //double size = Math.abs(this.sectorData.getMinX()) + Math.abs(this.sectorData.getMaxX()) + 2;
        var world = Objects.requireNonNull(Bukkit.getWorld(this.sectorData.getSectorType().getWorldName()));

        world.getWorldBorder().setCenter(x, z);
        world.getWorldBorder().setSize(size);

        //world.setViewDistance(this.sectorData.getViewDistance());
       // world.setSendViewDistance(this.sectorData.getSimulationDistance());

        this.sectorData.calibrateBorderDistance();

        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, false);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 3);

        if (this.sectorData.getSectorType().hasWeatherAndTimeSync() 
                && !this.sectorData.getSectorType().getWeatherAndTimeProvider().equals(this.plugin.getGlmcApiBukkit().getServerId())) {
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        }

        this.plugin.getLogger().info(ChatColor.GREEN + "Worldborder został ustawiony na x: " + x + ", z: " + z + ", a rozmiar na " + size);
    }

    public DistanceInfo distance(Location location) {
        double north = Math.abs(this.sectorData.getMinZ() - location.getZ());
        double south = Math.abs(this.sectorData.getMaxZ() - location.getZ());
        double east = Math.abs(this.sectorData.getMaxX() - location.getX());
        double west = Math.abs(this.sectorData.getMinX() - location.getX());

        double distance = Math.abs(Math.min(Math.min(north, south), Math.min(east, west)));

        if (distance == north) return new DistanceInfo(this.sectorData.getServerNorth(), "north", distance);
        if (distance == south) return new DistanceInfo(this.sectorData.getServerSouth(), "south", distance);
        if (distance == east) return new DistanceInfo(this.sectorData.getServerEast(), "east", distance);
        if (distance == west) return new DistanceInfo(this.sectorData.getServerWest(), "west", distance);

        return null;
    }

    private void boucePlayer(Player player, String direction) {
        if (player.getLocation().getBlock().getBlockData().getMaterial() != Material.AIR) {
            if (Objects.equals(direction, "north"))
                player.setVelocity(player.getLocation().toVector().subtract(player.getLocation().add(0, -0.3, -0.8).toVector()).multiply(1));
            if (Objects.equals(direction, "south"))
                player.setVelocity(player.getLocation().toVector().subtract(player.getLocation().add(0, -0.3, 0.8).toVector()).multiply(1));
            if (Objects.equals(direction, "east"))
                player.setVelocity(player.getLocation().toVector().subtract(player.getLocation().add(0.8, -0.3, 0).toVector()).multiply(1));
            if (Objects.equals(direction, "west"))
                player.setVelocity(player.getLocation().toVector().subtract(player.getLocation().add(-0.8, -0.3, 0).toVector()).multiply(1));
        } else {
            if (Objects.equals(direction, "north"))
                player.setVelocity(player.getLocation().toVector().subtract(player.getLocation().add(0, -0.2, -0.4).toVector()).multiply(1));
            if (Objects.equals(direction, "south"))
                player.setVelocity(player.getLocation().toVector().subtract(player.getLocation().add(0, -0.2, 0.4).toVector()).multiply(1));
            if (Objects.equals(direction, "east"))
                player.setVelocity(player.getLocation().toVector().subtract(player.getLocation().add(0.4, -0.2, 0).toVector()).multiply(1));
            if (Objects.equals(direction, "west"))
                player.setVelocity(player.getLocation().toVector().subtract(player.getLocation().add(-0.4, -0.2, 0).toVector()).multiply(1));
        }
    }

    private void sendBorderInfo(Player player, DistanceInfo distanceInfo) {
        if (this.distanceBossBars.containsKey(player.getUniqueId())) {
            this.distanceBossBars.remove(player.getUniqueId()).removeAll();
        }

        String server = distanceInfo.getServer();

        long distance = Math.round(distanceInfo.getDistance());

        //if (player.isInsideVehicle() && distance >= 1) distance -= 1;

        if (distance == 0) {
            if (server.equals("null")) {
                this.boucePlayer(player, distanceInfo.getDirection());
                player.sendMessage(ChatColor.RED + "Nie możesz przekroczyć granicy mapy!");
            }
            else if (this.plugin.getGlmcAntylogout().hasActiveAntyLogout(player.getUniqueId())) {
                this.boucePlayer(player, distanceInfo.getDirection());
                player.sendMessage(ChatColor.RED + "Nie możesz zmienić sektora podaczas antylogoutu!");
            } else {
                TransferMetaData transferMetaData = new TransferMetaData();
                this.plugin.getGlmcTransferProvider().getTransferService().transferPlayer(player.getUniqueId(), server, transferMetaData, TransferAPI.TransferReason.SERVER_TRANSFER, false).
                        thenAccept(transferResult -> player.sendMessage(ChatColor.GOLD + "result transferu: " + transferResult));
            }
        }

        if (distanceInfo.isMapBorder()) server = "GRANICY MAPY";

        ChatColor chatColor = ChatColor.GREEN;
        BarColor barColor = BarColor.GREEN;

        if (distance <= 15 && distance > 10) {
            chatColor = ChatColor.YELLOW;
            barColor = BarColor.YELLOW;
        } else if (distance <= 10 && distance > 5) {
            chatColor = ChatColor.RED;
            barColor = BarColor.RED;
        } else if (distance <= 5) {
            chatColor = ChatColor.DARK_PURPLE;
            barColor = BarColor.PURPLE;
        }

        BossBar bossBar = this.plugin.getServer().createBossBar(
                ChatColor.translateAlternateColorCodes('&', "&f&lDystans " + chatColor + "&l" + distance + "&f&l do " + chatColor + "&l" + server.toUpperCase()),
                barColor, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(distanceInfo.getDistance() / 30);
        bossBar.addPlayer(player);

        this.distanceBossBars.put(player.getUniqueId(), bossBar);
    }
}
