package pl.glmc.serverlinker.bungee.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RTPCmd extends Command {
    private final GlmcServerLinkerBungee plugin;

    public RTPCmd(GlmcServerLinkerBungee plugin) {
        super("rtp", "glmc.rtp", "randomtp");

        this.plugin = plugin;

        this.plugin.getProxy().getPluginManager().registerCommand(this.plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tej komendy może użyć tylko gracz!"));

            return;
        }

        var sector = this.plugin.getGlmcTransferProvider().getSectorManager().getSectors().values()
                .stream()
                .filter(sectorData -> !sectorData.getId().equals(player.getServer().getInfo().getName()) && sectorData.getSectorType().getId().equals("world"))
                .toList().get(new Random().nextInt(8));

        this.plugin.getGlmcTransferProvider().getRandomTeleportService().transferPlayerToRandomCoords(player, sector.getId(), true)
                .thenAccept(transferResult -> {
                    if (transferResult == TransferAPI.TransferResult.SUCCESS) {
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Pomyślnie przeniesiono losowo na serwer " + ChatColor.DARK_GREEN
                                + sector.getId() + ChatColor.GREEN + "!"));
                    } else {
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Nie udalo sie znalezc odpowiedniej lokalizacji, sprobuj ponownie! " + ChatColor.DARK_RED
                                + sector.getId() + ChatColor.RED + " (" + transferResult + ")!"));
                    }
                });
    }
}