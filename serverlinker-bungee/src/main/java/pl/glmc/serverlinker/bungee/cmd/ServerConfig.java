package pl.glmc.serverlinker.bungee.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.apache.commons.lang.StringUtils;
import pl.glmc.serverlinker.api.common.TransferAPI;
import pl.glmc.serverlinker.api.common.TransferMetaData;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ServerConfig extends Command implements TabExecutor {
    private final GlmcServerLinkerBungee plugin;

    public ServerConfig(GlmcServerLinkerBungee plugin) {
        super("server", "glmc.server", "serwer");

        this.plugin = plugin;

        this.plugin.getProxy().getPluginManager().registerCommand(this.plugin, this);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String arg = args[0];

            return this.plugin.getProxy().getServersCopy().keySet().stream().filter(param -> StringUtils.startsWithIgnoreCase(param, arg))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tej komendy może użyć tylko gracz!"));

            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (args.length == 1) {
            String serverTarget = args[0];
            ServerInfo serverTargetInfo = this.plugin.getProxy().getServerInfo(serverTarget);

            if (serverTargetInfo == null) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Serwer o podanej nazwie nie istnieje!"));

                return;
            }

            TransferMetaData transferMetaData = new TransferMetaData();
            this.plugin.getGlmcTransferProvider().getTransferService().transferPlayer(player.getUniqueId(), serverTarget, transferMetaData, TransferAPI.TransferReason.SERVER_TRANSFER, true)
                .thenAccept(transferResult -> {
                    if (transferResult == TransferAPI.TransferResult.SUCCESS) {
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Pomyślnie przeniesiono na serwer " + ChatColor.DARK_GREEN
                                + serverTargetInfo.getName() + ChatColor.GREEN + "!"));
                    } else {
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Wystąpił błąd podczas przenoszenia na serwer " + ChatColor.DARK_RED
                                + serverTargetInfo.getName() + ChatColor.RED + " (" + transferResult + ")!"));
                    }
                });
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Poprawne użycie komendy: /server <serwer>"));
        }
    }
}
