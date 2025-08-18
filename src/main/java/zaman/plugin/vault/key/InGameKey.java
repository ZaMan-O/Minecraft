package zaman.plugin.vault.key;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import zaman.plugin.vault.MainVaultPlugin;

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
        if (entity instanceof Interaction && entity.equals(plugin.keyInteraction)) {
            event.setCancelled(true);
            player.sendTitle("§e열쇠를 얻으셨습니다!", "§6목표 : 금고 열기", 10, 40, 10);
            player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_EJECT_ITEM, 0.65F, 1);

            ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
            ItemMeta meta = key.getItemMeta();
            meta.setDisplayName("§e금고 열쇠");
            meta.setEnchantmentGlintOverride(true);
            key.setItemMeta(meta);

            plugin.keyItem = key;
            for (World w : Bukkit.getWorlds()) {
                for (Chunk chunk : w.getLoadedChunks()) {
                    plugin.processVaultsInChunk(chunk);
                }
            }
            player.getInventory().addItem(key);

            plugin.keyDisplay.remove();
            plugin.keyInteraction.remove();

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.getInventory().setItem(8, new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onVaultLoot(BlockDispenseLootEvent event) {
        if (event.getBlock().getType() != Material.VAULT) return;

        event.setCancelled(true);

        var world = event.getBlock().getWorld();
        var loc = event.getBlock().getLocation().add(0.5, 1.0, 0.5);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255,255,0),1f);

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 25) {
                    cancel();
                    return;
                }
                world.playSound(loc, Sound.BLOCK_TRIAL_SPAWNER_EJECT_ITEM, 1.0f, 1.0f);
                world.spawnParticle(
                        Particle.DUST,
                        loc,
                        50,
                        1, 1, 1,
                        0,
                        dustOptions
                );
                world.dropItemNaturally(loc, new ItemStack(Material.GOLD_INGOT, 64));
                count++;
            }
        }.runTaskTimer(plugin, 0L, 4L);
    }

    @EventHandler
    public void keyDrop(PlayerDropItemEvent event) {
        if (plugin.isGameStarted()) {
            if (event.getItemDrop().getItemStack().equals(plugin.keyItem)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void keySwap(PlayerSwapHandItemsEvent event) {
        if (plugin.isGameStarted()) {
            ItemStack mainItem = event.getMainHandItem();
            ItemStack offItem = event.getOffHandItem();
            if (mainItem.equals(plugin.keyItem)) {
                event.setCancelled(true);
            } else if (offItem.equals(plugin.keyItem)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void keyPlace(BlockPlaceEvent event) {
        if(plugin.isGameStarted()) {
            ItemStack placedItem = event.getPlayer().getInventory().getItemInMainHand();
            if(placedItem.equals(plugin.keyItem)) {
                Block against = event.getBlockAgainst();
                if (against != null && against.getType() == Material.VAULT) {
                    event.setCancelled(true);
                    return;
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        for (BlockState state : chunk.getTileEntities()) {
            if (state.getType() != Material.VAULT) continue;

            org.bukkit.block.Vault vault = (org.bukkit.block.Vault) state;
            ItemStack current = vault.getKeyItem();
            if (current == null || !current.isSimilar(plugin.keyItem)) {
                vault.setKeyItem(plugin.keyItem.clone());
                vault.update(true, true);
            }
        }
    }
}