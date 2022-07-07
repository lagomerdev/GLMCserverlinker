package pl.glmc.serverlinker.bungee.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.glmc.serverlinker.bungee.GlmcServerLinkerBungee;

import java.util.Random;

public class TestCmd extends Command {
    private final GlmcServerLinkerBungee plugin;

    public TestCmd(GlmcServerLinkerBungee plugin) {
        super("test", "glmc.test");

        this.plugin = plugin;

        this.plugin.getProxy().getPluginManager().registerCommand(this.plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Tej komendy może użyć tylko gracz!"));

            return;
        }

        if (args.length == 1) {
            var player = this.plugin.getProxy().getPlayer(args[0]);
            if (player != null) {
                this.plugin.getGlmcTransferProvider().getPlayerService().stunPlayer(player, 5)
                        .thenAccept(result -> {
                            System.out.println(result);
                            sender.sendMessage(ChatColor.YELLOW + "Result: " + ChatColor.GOLD + result);
                        });
            }
        }
    }
}