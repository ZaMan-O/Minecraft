package zaman.plugin.vault.key;

import org.bukkit.ChatColor;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class InGameKey implements Listener {
    @EventHandler
    public void itemDisplayRightClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Interaction)) return;
        
        event.getPlayer().sendMessage(ChatColor.AQUA + "발광 다이아몬드 디스플레이를 우클릭했습니다!");
    }
}
