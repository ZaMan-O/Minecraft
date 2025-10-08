package zaman.plugin.vault.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zaman.plugin.vault.MainVaultPlugin;

public class CanVaultOpen implements CommandExecutor {
    private final MainVaultPlugin plugin;
    public CanVaultOpen(MainVaultPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "플레이어만 사용 가능한 커맨드입니다!");
            return true;
        }

        Player sender = (Player) commandSender;
        if(!(sender.isOp())) {
            commandSender.sendMessage(ChatColor.RED + "OP가 있어야 사용 가능한 커맨드입니다!");
            sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return true;
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle("§a금고를 열 수 있습니다.", "§61시간 30분이 지났습니다.");
            p.sendMessage("§a금고를 열 수 있습니다.");
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f);
        }
        plugin.canOpenVault = true;
        return true;
    }
}
