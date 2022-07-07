package pl.glmc.serverlinker.bukkit.api.transfer.listener.event;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import pl.glmc.serverlinker.bukkit.GlmcServerLinkerBukkit;
import pl.glmc.serverlinker.bukkit.api.transfer.ApiTransferService;

public class PlayerFreezeListener implements Listener {
    private final GlmcServerLinkerBukkit plugin;
    private final ApiTransferService transferService;

    public PlayerFreezeListener(GlmcServerLinkerBukkit plugin, ApiTransferService transferService) {
        this.plugin = plugin;
        this.transferService = transferService;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent moveEvent) {
        if (moveEvent.getFrom().getBlockX() == moveEvent.getTo().getBlockX()
            && moveEvent.getFrom().getBlockY() == moveEvent.getTo().getBlockY()
            && moveEvent.getFrom().getBlockZ() == moveEvent.getTo().getBlockZ()) {
            return;
        }

        Player player = moveEvent.getPlayer();

        if (this.transferService.isFrozen(player.getUniqueId())) {
            moveEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onMove]"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onItemPickup(PlayerAttemptPickupItemEvent pickupItemEvent) {
        Player player = pickupItemEvent.getPlayer();

        if (this.transferService.isFrozen(player.getUniqueId())) {
            pickupItemEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onItemPickup]"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent dropItemEvent) {
        Player player = dropItemEvent.getPlayer();

        if (this.transferService.isFrozen(player.getUniqueId())) {
            dropItemEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onItemDrop]"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryInteractEvent interactEvent) {
        Player player = (Player) interactEvent.getWhoClicked();

        if (this.transferService.isFrozen(player.getUniqueId())) {
            interactEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onInventoryClick]"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent openEvent) {
        Player player = (Player) openEvent.getPlayer();

        if (this.transferService.isFrozen(player.getUniqueId())) {
            openEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onInventoryOpen]"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBowShoot(EntityShootBowEvent shootBowEvent) {
        if (!(shootBowEvent.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) shootBowEvent.getEntity();

        if (this.transferService.isFrozen(player.getUniqueId())) {
            shootBowEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onBowShoot]"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent interactEvent) {
        Player player = interactEvent.getPlayer();

        if (this.transferService.isFrozen(player.getUniqueId())) {
            interactEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onInteract]"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent deathEvent) {
        Player player = deathEvent.getEntity();

        if (this.transferService.isFrozen(player.getUniqueId())) {
            deathEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onDeath]"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent damageEvent) {
        if (!(damageEvent.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) damageEvent.getEntity();

        if (this.transferService.isFrozen(player.getUniqueId())) {
            damageEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onDamage]"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onHealthRegen(EntityRegainHealthEvent regainHealthEvent) {
        if (!(regainHealthEvent.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) regainHealthEvent.getEntity();

        if (this.transferService.isFrozen(player.getUniqueId())) {
            regainHealthEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onHealthRegen]"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onItemConsume(PlayerItemConsumeEvent itemConsumeEvent) {
        Player player = itemConsumeEvent.getPlayer();

        if (this.transferService.isFrozen(player.getUniqueId())) {
            itemConsumeEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onItemConsume]"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEffectApply(EntityPotionEffectEvent potionEffectEvent) {
        if (!(potionEffectEvent.getEntity() instanceof Player player)) {
            return;
        }

        if (this.transferService.isFrozen(player.getUniqueId())) {
            potionEffectEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onEffectApply]"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onVehicleEnter(VehicleEnterEvent enterEvent) {
        if (!(enterEvent.getEntered() instanceof Player player)) {
            return;
        }

        if (this.transferService.isFrozen(player.getUniqueId())) {
            enterEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onVehicleEnter]"));
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onVehicleExit(VehicleExitEvent exitEvent) {
        if (!(exitEvent.getExited() instanceof Player player)) {
            return;
        }

        if (this.transferService.isFrozen(player.getUniqueId())) {
            exitEvent.setCancelled(true);

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Transfer freeze! " + ChatColor.DARK_RED + "[onVehicleExit]"));
        }
    }
}
