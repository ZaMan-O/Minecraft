package zaman.plugin.vault;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import zaman.plugin.vault.command.GameStartCommand;
import zaman.plugin.vault.compass.KeyCompassSetting;
import zaman.plugin.vault.key.InGameKey;
import zaman.plugin.vault.key.KeyInMapSystem;

public final class MainVaultPlugin extends JavaPlugin implements Listener {
    private boolean gameStatus = false;
    public Integer key_m;
    public Integer key_x;

    public int x;
    public int z;

    public void setKeySpawnXZ(Integer key_m, Integer key_x) {
        this.key_m = key_m;
        this.key_x = key_x;
    }

    public void setKeySpawnPosition(Integer key_x, Integer key_z) {
        this.x = key_x;
        this.z = key_z;
        getLogger().info("x와 z : " + this.x + this.z);
    }

    public boolean isGameStarted() {
        return gameStatus;
    }

    public void startGame() {
        this.gameStatus = true;
        KeyCompassSetting compassSetting = new KeyCompassSetting(this);
        compassSetting.setCompass();

        KeyInMapSystem keyInMapSystem = new KeyInMapSystem(this);
    }

    public void endGame() {
        this.gameStatus = false;
    }

    @Override
    public void onEnable() {
        getCommand("게임").setExecutor(new GameStartCommand(this));
        getLogger().info("VaultPlugin enabled");
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new InGameKey(this), this);
    }

    // 나침반 버리기 방지
    @EventHandler
    public void compassClick(InventoryClickEvent event) {
        if(isGameStarted() && event.getSlot() == 8) {
            ItemStack item = event.getCurrentItem();
            if(item.getType().equals(Material.COMPASS)) {
                ItemMeta meta = item.getItemMeta();
                if(meta.hasDisplayName()) {
                    String displayName = meta.getDisplayName();
                    if(displayName.equals("§e열쇠 위치 나침반")) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void compassDrop(PlayerDropItemEvent event) {
        if(isGameStarted()) {
            ItemStack item = event.getItemDrop().getItemStack();
            if(item.getType().equals(Material.COMPASS)) {
                ItemMeta meta = item.getItemMeta();
                if(meta.hasDisplayName()) {
                    String displayName = meta.getDisplayName();
                    if(displayName.equals("§e열쇠 위치 나침반")) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void compassSwap(PlayerSwapHandItemsEvent event) {
        if(isGameStarted()) {
            ItemStack mainItem = event.getMainHandItem();
            ItemStack offItem = event.getOffHandItem();
            if(mainItem.getType().equals(Material.COMPASS)) {
                ItemMeta meta = mainItem.getItemMeta();
                if(meta.hasDisplayName()) {
                    String displayName = meta.getDisplayName();
                    if(displayName.equals("§e열쇠 위치 나침반")) {
                        event.setCancelled(true);
                    }
                }
            } else if(offItem.getType().equals(Material.COMPASS)) {
                ItemMeta meta = offItem.getItemMeta();
                if(meta.hasDisplayName()) {
                    String displayName = meta.getDisplayName();
                    if(displayName.equals("§e열쇠 위치 나침반")) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}