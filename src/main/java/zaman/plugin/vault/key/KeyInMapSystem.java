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

        Location cloc = new Location(world, x + 0.5, 0, z + 0.5); // 블록 중앙에 맞추기
        // 청크 로드
        if (!cloc.getChunk().isLoaded()) {
            cloc.getChunk().load(true);
        }

        ItemDisplay keyDisplay = world.spawn(
                cloc, ItemDisplay.class, entity -> {
                    entity.setItemStack(new ItemStack(Material.TRIPWIRE_HOOK));
                    entity.setGlowing(true);
                    entity.setPersistent(true);
                    entity.setCustomNameVisible(false);
                    entity.addScoreboardTag("key");

                    Transformation transformation = entity.getTransformation();
                    transformation.getScale().set(1.5f,1.5f,1.5f);
                    entity.setTransformation(transformation);
                }
        );
        Interaction keyInteraction = world.spawn(
                cloc, Interaction.class, entity -> {
                    entity.setInteractionHeight(2.0f);
                    entity.setInteractionWidth(2.0f);
                    entity.setPersistent(true);
                    entity.addScoreboardTag("keyInteraction");
                }
        );
    }
}
