package zaman.plugin.vault.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zaman.plugin.vault.MainVaultPlugin;

public class GettingKeyCommand implements CommandExecutor {
    private MainVaultPlugin plugin;

    public GettingKeyCommand(MainVaultPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.isOp()) return false;
        if(plugin.keyItem == null) return false;
        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if(plugin.keyDisplay != null) plugin.keyDisplay.remove();
        if(plugin.keyInteraction != null) plugin.keyInteraction.remove();

        if(!plugin.keyStatus && plugin.havingKeyPlayer != null) {
            plugin.havingKeyPlayer.getInventory().removeItem(plugin.keyItem);
        }

        player.getInventory().addItem(plugin.keyItem);

        plugin.keyStatus = false;
        plugin.havingKeyPlayer = player;
        return true;
    }
}