package xyz.yooniks.enchants;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import xyz.yooniks.enchants.InventoryManager.BookEnchantment;
import xyz.yooniks.enchants.InventoryManager.BookItem;

public class InventoryListener implements Listener {

  private final InventoryManager inventoryManager;

  public InventoryListener(InventoryManager inventoryManager) {
    this.inventoryManager = inventoryManager;
  }

  @EventHandler(ignoreCancelled = true)
  public void onEnchantOpen(InventoryOpenEvent e) {
    if (e.getInventory() == null || !(e.getPlayer() instanceof Player)) {
      return;
    }

    final Inventory inv = e.getInventory();
    if (inv.getType() == InventoryType.ENCHANTING) {
      e.setCancelled(true);

      final Player player = ((Player) e.getPlayer());
      player.openInventory(
          this.inventoryManager.getInventory(player,
              this.getBookshelvesAmount(player.getLocation(), 10)) //how about this radius?
      );
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.getInventory() == null || event.getClickedInventory() == null ||
        !(event.getWhoClicked() instanceof Player)) {
      return;
    }

    if (!event.getClickedInventory().getTitle().equalsIgnoreCase(this.inventoryManager.getName())) {
      return;
    }
    if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
      return;
    }

    final Player player = ((Player) event.getWhoClicked());
    if (player.getInventory().getItemInHand() == null) {
      player.sendMessage(ChatColor.DARK_RED + "You have to hold something in hand!");
      player.closeInventory();
      return;
    }

    final int bookshelfs = Integer.parseInt(event.getClickedInventory().getTitle().split("&c")[0]);

    for (BookItem bookItem : this.inventoryManager.getItems()) {
      if (bookItem.getSlot() == event.getSlot()) {
        if (player.getLevel() < bookItem.getLevel()) {
          player.sendMessage(ChatColor.DARK_RED +
              "You need: " + bookItem.getLevel() + " level to buy this enchant!!");
          return;
        } else if (bookshelfs < bookItem.getBookshelves()) {
          player.sendMessage(ChatColor.DARK_RED + "You need: " + bookItem.getBookshelves()
              + " bookshelves around the enchant!");
          return;
        }

        player.setLevel(player.getLevel() - bookItem.getLevel());

        final BookEnchantment bookEnchantment = bookItem.getEnchantment();

        player.getInventory().getItemInHand().getItemMeta().addEnchant(
            bookEnchantment.getEnchantment(), bookEnchantment.getLevel(), true);

        player.sendMessage(ChatColor.GREEN + "You have bought enchant: "
            + bookEnchantment.getEnchantment().getName() + ":" + bookEnchantment.getLevel());
        player.sendMessage(ChatColor.GOLD + "That took you " + bookItem.getLevel() + " levels!");

        player.closeInventory();
        return;
      }
    }
  }

  private int getBookshelvesAmount(Location location, int radius) {
    int count = 0;
    for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
      for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
        for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
          if (location.getWorld().getBlockAt(x, y, z).getType() == Material.BOOKSHELF) {
            count++;
          }
        }
      }
    }
    return count;
  }

}
