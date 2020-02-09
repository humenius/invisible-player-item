package me.humenius.spigot.invisplayeritem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
/**
 * <h1>InvisiblePlayerItem</h1>
 * <p>A plugin that creates an item making everyone invisible except themselves upon interaction.</p>
 *
 * @author humenius
 * @since 1.0.0
 */
public class Main extends JavaPlugin implements Listener {
    private final Logger log = getLogger();
    private final InvisiblePlayerItem item = new InvisiblePlayerItem(new ItemStack(Material.BOOK, 1), this);

    @Override
    public void onDisable() {
        log.info("Plugin disabled.");
    }

    @Override
    public void onEnable() {
        log.info("Plugin enabled.");

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(item, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Inventory inventory = event.getPlayer().getInventory();
        if (inventory.contains(item.getItemStack(true)) || inventory.contains(item.getItemStack(false))) return;
        inventory.setItem(0, item.getItemStack(false));
    }
}
