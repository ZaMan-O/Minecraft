package zaman.plugin.vault.key;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import zaman.plugin.vault.MainVaultPlugin;

public class TargetingPlayer implements Listener {
    MainVaultPlugin plugin;

    public TargetingPlayer(MainVaultPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void targetPlayer(PlayerSwapHandItemsEvent event) {
        if(plugin.isGameStarted()) {
            Player player = event.getPlayer();
            ItemStack mainItem = player.getInventory().getItemInMainHand();
            if(mainItem != null && mainItem.getType() == Material.DIAMOND) {
                if(plugin.havingKeyPlayer == null) {
                    player.sendMessage(ChatColor.RED + "열쇠를 가지고 있는 플레이어가 없습니다!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    event.setCancelled(true);
                    return;
                }
                World world = null;
                if(plugin.keyStatus) world = plugin.keyLocation.getWorld();
                else world = plugin.havingKeyPlayer.getWorld();

                if(!player.getWorld().equals(world)) {
                    player.sendMessage(ChatColor.RED + "열쇠를 가진 플레이어가 다른 차원에 있습니다.");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    event.setCancelled(true);
                    return;
                }
                if(mainItem.getAmount() - 1 <= 0) {
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                } else {
                    mainItem.setAmount(mainItem.getAmount() - 1);
                    player.getInventory().setItemInMainHand(mainItem);
                }
                event.setCancelled(true);
                Location loc = plugin.getKeyLocation();
                player.getWorld().spawnParticle(
                        Particle.TRAIL,
                        player.getLocation().clone().add(0, 1.5 ,0),
                        25,
                        0.5, 0.5, 0.5,
                        new Particle.Trail(loc, Color.AQUA, (int)(player.getLocation().distance(loc) * 1.5))
                );
            }
        }
    }
}
