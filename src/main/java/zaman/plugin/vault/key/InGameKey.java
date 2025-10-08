package zaman.plugin.vault.key;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
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
            if (!plugin.keyStatus) {
                event.setCancelled(true);
                player.sendTitle("§e열쇠를 얻으셨습니다!", "§6목표 : 금고 열기", 10, 40, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_EJECT_ITEM, 0.65F, 1);
                plugin.havingKeyPlayer = player;

                ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
                ItemMeta meta = key.getItemMeta();
                meta.setDisplayName("§e금고 열쇠");
                meta.setEnchantmentGlintOverride(true);
                key.setItemMeta(meta);

                plugin.keyItem = key;
                player.getInventory().addItem(key);

                plugin.keyDisplay.remove();
                plugin.keyInteraction.remove();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p != player) {
                        p.sendTitle("§c열쇠를 누가 가져갔습니다!", "§e빠르게 쫓아가세요!", 5, 60, 5);
                        p.sendMessage("§c열쇠를 누가 가져갔습니다! §e빠르게 쫓아가세요!");
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.25f, 0.5f);
                    p.getInventory().setItem(8, new ItemStack(Material.AIR));
                }
            } else {
                event.setCancelled(true);
                player.sendTitle("§e열쇠를 얻으셨습니다!", "§6목표 : 금고 열기", 10, 40, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_EJECT_ITEM, 0.65F, 1);
                plugin.havingKeyPlayer = player;
                plugin.keyStatus = false;

                player.getInventory().addItem(plugin.keyItem);

                plugin.keyDisplay.remove();
                plugin.keyInteraction.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.equals(plugin.havingKeyPlayer)) {
            player.getInventory().removeItem(plugin.keyItem);

            World world = Bukkit.getWorld("world");
            Location loc = player.getLocation();
            Location loc2 = player.getLocation().clone().add(0, -0.75, 0);
            plugin.keyLocation = loc;

            plugin.getLogger().info("아이템 디스플레이 소환중..");
            ItemDisplay keyDisplay = world.spawn(
                    loc, ItemDisplay.class, entity -> {
                        entity.setItemStack(new ItemStack(Material.TRIPWIRE_HOOK));
                        entity.setGlowing(true);
                        entity.setPersistent(true);
                        entity.setCustomNameVisible(false);

                        Transformation transformation = entity.getTransformation();
                        transformation.getScale().set(2.0f, 2.0f, 2.0f);
                        entity.setTransformation(transformation);
                    }
            );
            plugin.getLogger().info("아이템 디스플레이 소환 완료!");
            plugin.getLogger().info("인터렉션 소환중..");
            Interaction keyInteraction = world.spawn(
                    loc2, Interaction.class, entity -> {
                        entity.setInteractionHeight(1.5f);
                        entity.setInteractionWidth(1.5f);
                        entity.setPersistent(true);
                    }
            );
            plugin.getLogger().info("인터렉션 소환 완료!");

            plugin.keyStatus = true;
            plugin.setKeyEntity(keyDisplay, keyInteraction);
        }
    }

    @EventHandler
    public void onVaultLoot(PlayerInteractEntityEvent event) {
        if (!plugin.canOpenVault) return;
        if (!event.getPlayer().getInventory().getItemInMainHand().equals(plugin.keyItem)) return;
        Entity entity = event.getRightClicked();
        if (!entity.getScoreboardTags().contains("vaultInt")) return;

        event.setCancelled(true);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 255, 0), 1f);

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle("§e" + event.getPlayer().getName(), "§e님이 금고를 열었습니다!", 5, 100 , 5);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 0.2f, 1f);
            if(p.equals(event.getPlayer())) continue;
            p.teleport(event.getPlayer());
        }
        plugin.endGame();

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 15) {
                    cancel();
                    return;
                }
                entity.getWorld().playSound(entity.getLocation().clone(),
                        Sound.BLOCK_TRIAL_SPAWNER_EJECT_ITEM,
                        1.0f,
                        1.0f);
                entity.getWorld().spawnParticle(
                        Particle.DUST,
                        entity.getLocation().clone(),
                        50,
                        1, 1, 1,
                        0,
                        dustOptions
                );
                entity.getWorld().dropItemNaturally(entity.getLocation().clone().add(0, 1, 0),
                        new ItemStack(
                                Material.GOLD_INGOT,
                                64));
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
        if (plugin.isGameStarted()) {
            ItemStack placedItem = event.getPlayer().getInventory().getItemInMainHand();
            if (placedItem.equals(plugin.keyItem)) {
                Block against = event.getBlockAgainst();
                if (against != null && against.getType() == Material.VAULT) {
                    event.setCancelled(true);
                    return;
                }
                event.setCancelled(true);
            }
        }
    }
}