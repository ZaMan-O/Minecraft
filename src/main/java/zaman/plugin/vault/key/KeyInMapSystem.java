package zaman.plugin.vault.key;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import zaman.plugin.vault.MainVaultPlugin;

public class KeyInMapSystem {
    private MainVaultPlugin plugin;
    public KeyInMapSystem(MainVaultPlugin plugin) { this.plugin = plugin; }

    public void spawnKey() {
        World world = Bukkit.getWorld("world");
        int x = plugin.x;
        int z = plugin.z;
        Location loc = new Location(world,x,0,z);
        Location loc2 = new Location(world,x,-0.75,z);

        loc.getChunk().load(true);
        plugin.getLogger().info("아이템 디스플레이 소환중..");
        ItemDisplay keyDisplay = world.spawn(
                loc, ItemDisplay.class, entity -> {
                    entity.setItemStack(new ItemStack(Material.TRIPWIRE_HOOK));
                    entity.setGlowing(true);
                    entity.setPersistent(true);
                    entity.setCustomNameVisible(false);

                    Transformation transformation = entity.getTransformation();
                    transformation.getScale().set(2.0f,2.0f,2.0f);
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
                    entity.addScoreboardTag("thisiskey");
                }
        );
        plugin.getLogger().info("인터렉션 소환 완료!");

        plugin.setKeyEntity(keyDisplay, keyInteraction);
    }
}
