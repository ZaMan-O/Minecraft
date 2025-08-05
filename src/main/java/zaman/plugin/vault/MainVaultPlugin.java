package zaman.plugin.vault;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import zaman.plugin.vault.command.GameStartCommand;
import zaman.plugin.vault.compass.keyCompassSetting;

public final class MainVaultPlugin extends JavaPlugin implements Listener {
    private boolean gameStatus = false;
    private Integer key_m;
    private Integer key_x;

    public void setKeySpawnXZ(Integer key_m, Integer key_x) {
        this.key_m = key_m;
        this.key_x = key_x;
    }

    public boolean isGameStarted() {
        return gameStatus;
    }

    public void startGame() {
        this.gameStatus = true;
        keyCompassSetting compassSetting = new keyCompassSetting();
        compassSetting.setCompass(key_m,key_x);
    }

    public void endGame() {
        this.gameStatus = false;
    }

    @Override
    public void onEnable() {
        getCommand("게임").setExecutor(new GameStartCommand(this));
        getLogger().info("VaultPlugin enabled");
        Bukkit.getPluginManager().registerEvents(this, this);
    }
}