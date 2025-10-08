package zaman.plugin.vault;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Vault;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import zaman.plugin.vault.command.CanVaultOpen;
import zaman.plugin.vault.command.GameStartCommand;
import zaman.plugin.vault.compass.KeyCompassSetting;
import zaman.plugin.vault.key.InGameKey;
import zaman.plugin.vault.key.KeyInMapSystem;
import zaman.plugin.vault.key.TargetingPlayer;

public final class MainVaultPlugin extends JavaPlugin implements Listener {
    private boolean gameStatus = false;
    public boolean canOpenVault = false;
    public Player havingKeyPlayer = null;
    /* false = 아무 상태 X, true = 열쇠가 드롭되어 있는 상태 */
    public boolean keyStatus = false;
    public Location keyLocation = null;

    public Integer key_m;
    public Integer key_x;

    public int x;
    public int z;

    public ItemDisplay keyDisplay;
    public Interaction keyInteraction;

    public ItemStack keyItem;

    public Location getKeyLocation() {
        if(havingKeyPlayer != null) {
            if(keyStatus) {
                return keyLocation;
            } else {
                return havingKeyPlayer.getLocation();
            }
        }
        return null;
    }

    public void setKeySpawnXZ(Integer key_m, Integer key_x) {
        this.key_m = key_m;
        this.key_x = key_x;
    }

    public void setKeySpawnPosition(Integer key_x, Integer key_z) {
        this.x = key_x;
        this.z = key_z;
    }

    public void setKeyEntity(ItemDisplay keyDisplay, Interaction keyInteraction) {
        this.keyDisplay = keyDisplay;
        this.keyInteraction = keyInteraction;
    }

    public boolean isGameStarted() {
        return gameStatus;
    }

    public void startGame() {
        this.gameStatus = true;
        this.canOpenVault = false;
        this.havingKeyPlayer = null;
        this.keyStatus = false;
        this.keyLocation = null;
        KeyCompassSetting compassSetting = new KeyCompassSetting(this);
        compassSetting.setCompass();

        KeyInMapSystem keyInMapSystem = new KeyInMapSystem(this);
        keyInMapSystem.spawnKey();
    }

    public void endGame() {
        keyDisplay.remove();
        keyInteraction.remove();
        this.gameStatus = false;
        this.canOpenVault = false;
        this.havingKeyPlayer = null;
        this.keyStatus = false;
        this.keyLocation = null;
    }

    @Override
    public void onEnable() {
        getCommand("게임").setExecutor(new GameStartCommand(this));
        getCommand("stovo").setExecutor(new CanVaultOpen(this));
        getLogger().info("VaultPlugin enabled");
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new InGameKey(this), this);
        Bukkit.getPluginManager().registerEvents(new TargetingPlayer(this), this);
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