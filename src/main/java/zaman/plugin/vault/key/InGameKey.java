package zaman.plugin.vault.key;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import zaman.plugin.vault.MainVaultPlugin;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public class InGameKey implements Listener {
    private MainVaultPlugin plugin;
    public InGameKey(MainVaultPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void itemDisplayRightClick(PlayerInteractEntityEvent event) {
        World world = Bukkit.getWorld("world");
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (entity instanceof Interaction && entity.getScoreboardTags().contains("keyInteraction")) {
            event.setCancelled(true);
            player.sendTitle("§e열쇠를 얻으셨습니다!","§6목표 : 금고 열기",10,40,10);
            player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_EJECT_ITEM, 0.65F, 1);
            Collection<Entity> nearby = Objects.requireNonNull(world)
                    .getNearbyEntities(new Location(world,plugin.x,0,plugin.z),
                            2.0, 2.0, 2.0,
                            e -> e instanceof ItemDisplay && e.getScoreboardTags().contains("key"));
            for(Entity e : nearby) {
                e.remove();
            }
        }
    }
}
