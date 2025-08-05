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

public class GameStartCommand implements CommandExecutor {
    private final MainVaultPlugin plugin;

    public GameStartCommand(MainVaultPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        // 기본 조건
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "플레이어만 사용 가능한 커맨드입니다!");
            return true;
        }

        Player sender = (Player) commandSender;

        if(!(commandSender.isOp())) {
            commandSender.sendMessage(ChatColor.RED + "OP가 있어야 사용 가능한 커맨드입니다!");
            sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return true;
        }
        if(args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "사용법 : /게임 <시작/종료> <x,z최소> <x,z최대>");
            sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return true;
        }

        // 게임 시작 부분
        switch(args[0].toLowerCase()) {
            case "시작" -> {
                if(args.length < 2) {
                    commandSender.sendMessage(ChatColor.RED + "사용법 : /게임 <시작/종료> <x,z최소> <x,z최대>");
                    sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    return true;
                }

                if(!(isInteger(args[1]) && isInteger(args[2]))) {
                    commandSender.sendMessage(ChatColor.RED + "사용법 : /게임 <시작/종료> <x,z최소> <x,z최대>");
                    sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    return true;
                }

                if(plugin.isGameStarted()) {
                    commandSender.sendMessage(ChatColor.RED + "이미 게임이 시작됐습니다!");
                    sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                } else {
                    plugin.setKeySpawnXZ(Integer.parseInt(args[1]), Integer.parseInt(args[2]));

                    for(Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle("§a게임이 시작되었습니다!","§e과연 누가 먼저 금고를 열까요?",10,50,10);
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.65F, 1);
                    }

                    plugin.startGame();
                }
            }
            case "종료" -> {
                if(!(plugin.isGameStarted())) {
                    commandSender.sendMessage(ChatColor.RED + "시작된 게임이 없습니다.");
                    sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                } else {
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle("§e게임이 종료되었습니다!","§c" + sender.getName() + "님이 종료하였습니다.",10,50,10);
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.65F, 1);
                    }
                    plugin.endGame();
                }
            }
            default -> {
                commandSender.sendMessage(ChatColor.RED + "사용법 : /게임 <시작/종료> <x,z최소> <x,z최대>");
                sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            }
        }
        return false;
    }

    public boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
