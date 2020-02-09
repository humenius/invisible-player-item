package me.humenius.spigot.invisplayeritem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>InvisiblePlayerItem</h1>
 * <p>Represent item handling of actual {@link ItemStack} for toggling invisibility of other players.</p>
 *
 * @author humenius
 */
public class InvisiblePlayerItem implements Listener {
    public static final String METADATA_KEY = "invisplayeritem_isCurrentlyHidden";

    /** These fields contain equal ItemStack, one for toggle on and other for toggle off */
    private final ItemStack itemStack0, itemStack1;

    private final List<String> lore = new ArrayList<>();
    private final Plugin pluginRef;

    public InvisiblePlayerItem(ItemStack itemStack, Plugin pluginRef) {
        this.itemStack0 = itemStack;
        this.itemStack1 = itemStack.clone();
        this.pluginRef = pluginRef;
        setupItemStack();
    }

    @EventHandler
    public void onInteractionEvent(PlayerInteractEvent event) {
        if (!(event.hasItem() && (event.getItem().isSimilar(itemStack0) || event.getItem().isSimilar(itemStack1))))
            return;

        Player player = event.getPlayer();
        List<MetadataValue> metadataList = player.getMetadata(METADATA_KEY);

        if (metadataList != null && !metadataList.isEmpty()) {
            boolean isUsing = metadataList.get(0).asBoolean();
            player.sendMessage("Spieler sind jetzt " + (isUsing ? "sichtbar" : "unsichtbar"));

            hidePlayer(player, !isUsing);
            player.setMetadata(METADATA_KEY, new FixedMetadataValue(pluginRef, !isUsing));
            player.getInventory().setItem(0, getItemStack(!isUsing));
        } else {
            player.sendMessage("Spieler sind jetzt unsichtbar");
            hidePlayer(player, true);
            player.setMetadata(METADATA_KEY, new FixedMetadataValue(pluginRef, true));
            player.getInventory().setItem(0, itemStack1);
        }

        event.setCancelled(true);
    }

    public ItemStack getItemStack(boolean activated) {
        return (activated ? itemStack0 : itemStack1);
    }

    /**
     * Hides given player from each other who are online on the server.
     * @param player to be hidden from all other players and vice versa
     * @param hide show or hide player
     */
    private void hidePlayer(Player player, boolean hide) {
        if (hide) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                player.hidePlayer(p);
                p.hidePlayer(player);
            }
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                List<MetadataValue> metadataList = p.getMetadata(METADATA_KEY);
                if (metadataList != null && !metadataList.isEmpty()) {
                    if (metadataList.get(0).asBoolean())
                        continue;
                }

                player.showPlayer(p);
                p.showPlayer(player);
            }
        }
    }

    private String getItemDisplayName(boolean activated) {
        return "Spieler " + (activated ? "sichtbar" : "unsichtbar");
    }

    private void setupItemStack() {
        ItemMeta meta = itemStack0.getItemMeta();

        meta.setDisplayName(getItemDisplayName(false));
        lore.add("Wird dieses Item verwendet,");
        lore.add("dann werden alle Spieler erscheinen oder verschwinden.");
        meta.setLore(lore);
        itemStack0.setItemMeta(meta);

        meta.setDisplayName(getItemDisplayName(true));
        itemStack1.setItemMeta(meta);
    }
}
