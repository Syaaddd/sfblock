package core.sg.fixSfBug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class FixSfBug extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    private static final Set<String> restrictedGUIs = new HashSet<>();
    static {
        restrictedGUIs.add("Auto Enchanter - II");
        restrictedGUIs.add("Auto Disenchanter - II");
        restrictedGUIs.add("Auto Enchanter");
        restrictedGUIs.add("Auto Disenchanter");
        restrictedGUIs.add("Advanced Anvil");
        restrictedGUIs.add("Improvement Forge");
        restrictedGUIs.add("Mini Fluffy Barrel");
        restrictedGUIs.add("Small Fluffy Barrel");
        restrictedGUIs.add("Medium Fluffy Barrel");
        restrictedGUIs.add("Big Fluffy Barrel");
        restrictedGUIs.add("Large Fluffy Barrel");
        restrictedGUIs.add("Massive Fluffy Barrel");
        restrictedGUIs.add("Bottomless Fluffy Barrel");
        restrictedGUIs.add("Network Quantum Storage (4K)");
        restrictedGUIs.add("Network Quantum Storage (32K)");
        restrictedGUIs.add("Network Quantum Storage (262K)");
        restrictedGUIs.add("Network Quantum Storage (2M)");
        restrictedGUIs.add("Network Quantum Storage (16M)");
        restrictedGUIs.add("Network Quantum Storage (134M)");
        restrictedGUIs.add("Network Quantum Storage (1B)");
        restrictedGUIs.add("Network Quantum Storage (∞)");
    }

    private static final String[] restrictedLoreKeywords = {
            "Machine", "Right Click"
    };

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack clickedItem = event.getCurrentItem();
        String inventoryTitle = ChatColor.stripColor(event.getView().getTitle());

        if (clickedInventory == null || clickedItem == null) return;

        if (restrictedGUIs.contains(inventoryTitle)) {
            if (isRestrictedItem(clickedItem)) {
                event.setCancelled(true);
                sendLimitedMessage(player, ChatColor.RED + "You cannot interact with this item!");
            }
        }

        if (event.getClick() == ClickType.NUMBER_KEY) {
            int hotbarButton = event.getHotbarButton();
            if (hotbarButton >= 0 && hotbarButton <= 8) {
                ItemStack hotbarItem = player.getInventory().getItem(hotbarButton);
                if (hotbarItem != null && isRestrictedItem(hotbarItem)) {
                    event.setCancelled(true);
                    sendLimitedMessage(player, ChatColor.RED + "You cannot move this item into the GUI!");
                }
            }
        }

        if (event.getAction().name().contains("PLACE_ALL") && event.getSlotType() == InventoryType.SlotType.CONTAINER) {
            if (restrictedGUIs.contains(inventoryTitle)) {
                event.setCancelled(true);
                sendLimitedMessage(player, ChatColor.RED + "You cannot move items into this GUI!");
            }
        }

        if (clickedInventory.getType() == InventoryType.SMITHING && isRestrictedItem(clickedItem)) {
            event.setCancelled(true);
            sendLimitedMessage(player, ChatColor.RED + "You cannot use this item in the Smithing Table!");
        }
    }

    private boolean isRestrictedItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return false;

        List<String> loreList = meta.getLore();
        if (loreList == null) return false;

        for (String line : loreList) {
            String stripped = ChatColor.stripColor(line);
            for (String keyword : restrictedLoreKeywords) {
                if (stripped.contains(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sendLimitedMessage(Player player, String message) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("NamaPluginAnda");
        if (plugin == null) return;

        if (!player.hasMetadata("lastMessage")) {
            player.setMetadata("lastMessage", new org.bukkit.metadata.FixedMetadataValue(plugin, System.currentTimeMillis()));
            player.sendMessage(message);
        } else {
            long lastMessageTime = player.getMetadata("lastMessage").get(0).asLong();
            if (System.currentTimeMillis() - lastMessageTime > 10000) {
                player.setMetadata("lastMessage", new org.bukkit.metadata.FixedMetadataValue(plugin, System.currentTimeMillis()));
                player.sendMessage(message);
            }
        }
    }
}
