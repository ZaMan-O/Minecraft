package zaman.plugin.vault.compass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import java.util.concurrent.ThreadLocalRandom;

public class keyCompassSetting {
    public void setCompass(Integer key_m, Integer key_x) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta)compass.getItemMeta();

        ThreadLocalRandom random = ThreadLocalRandom.current();

        int x = random.nextInt(key_m,key_x);
        int z = random.nextInt(key_m,key_x);
        
        boolean random_1 = random.nextBoolean();
        boolean random_2 = random.nextBoolean();

        if(random_1) { x = -x; }
        if(random_2) { z = -z; }

        meta.setDisplayName("§e열쇠 위치 나침반");
        meta.setLodestone(new Location(Bukkit.getWorld("world"), x, 0, z));
        meta.setLodestoneTracked(false);
        compass.setItemMeta(meta);

        // 나침반 지급
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(compass);
        }
    }
}
