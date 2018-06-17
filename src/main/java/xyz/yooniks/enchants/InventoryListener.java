package xyz.yooniks.enchants;

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

      final Player p = ((Player) e.getPlayer());
      p.openInventory(
          this.inventoryManager.getInventory(p,
              this.getBookshelfs(p.getLocation(), 10)) //how about this radius?
      );
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent e) {
    if (e.getInventory() == null || e.getClickedInventory() == null ||
        !(e.getWhoClicked() instanceof Player)) {
      return;
    }

    if (!e.getClickedInventory().getTitle().equalsIgnoreCase(this.inventoryManager.getName())) {
      return;
    }
    if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) {
      return;
    }

    final Player p = ((Player) e.getWhoClicked());
    if (p.getInventory().getItemInHand() == null) {
      p.sendMessage("Musisz trzymac cos w rece!");
      p.closeInventory();
      return;
    }

    final int bookshelfs = Integer.parseInt(e.getClickedInventory().getTitle().split("&c")[0]);

    for (BookItem bookItem : this.inventoryManager.getItems()) {
      if (bookItem.getSlot() == e.getSlot()) {
        if (p.getLevel() < bookItem.getLevel()) {
          p.sendMessage("Potrzebujesz: " + bookItem.getLevel() + " lvl'a do zakupu tego enchantu!");
          return;
        } else if (bookshelfs < bookItem.getBookshelfs()) {
          p.sendMessage("Potrzebujesz: " + bookItem.getBookshelfs()
              + " biblioteczek wokol stolu do zaklinania!");
          return;
        }

        p.setLevel(p.getLevel() - bookItem.getLevel());

        final BookEnchantment bookEnchantment = bookItem.getEnchantment();

        p.getInventory().getItemInHand().getItemMeta().addEnchant(
            bookEnchantment.getEnchantment(), bookEnchantment.getLevel(), true);

        p.sendMessage("Zakupiles enchant: " + bookItem.getLevel());

        p.closeInventory();
        return;
      }
    }
  }

  private int getBookshelfs(Location location, int radius) {
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
