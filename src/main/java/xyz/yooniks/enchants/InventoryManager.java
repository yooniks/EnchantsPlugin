package xyz.yooniks.enchants;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryManager {

  private final String name;
  private final int size;
  private final List<BookItem> items;

  public InventoryManager(String name, int size, List<BookItem> items) {
    this.name = name;
    this.size = size;
    this.items = items;
  }

  public Inventory getInventory(Player player, int bookshelves) {
    final Inventory inv = Bukkit.createInventory(player, this.size,
        StringUtils.replace(this.name, "%bookshelves%", String.valueOf(bookshelves)));

    this.items.stream()

        .filter(bookItem ->
            bookshelves >= bookItem.getBookshelves()
                && player.getLevel() >= bookItem.getLevel())

        .forEach(bookItem ->
            inv.setItem(bookItem.getSlot(), bookItem.getItem()));

    return inv;
  }

  public String getName() {
    return name;
  }

  public List<BookItem> getItems() {
    return items;
  }

  public static class BookItem {

    private final int slot;
    private final ItemStack item;
    private final int level, bookshelves;
    private final BookEnchantment enchantment;

    public BookItem(int slot, ItemStack item, int level, int bookshelves,
        BookEnchantment enchantment) {
      this.slot = slot;
      this.item = item;
      this.level = level;
      this.bookshelves = bookshelves;
      this.enchantment = enchantment;
    }

    public int getSlot() {
      return slot;
    }

    public ItemStack getItem() {
      /* final ItemStack item = this.item.clone();
      final ItemMeta im = item.getItemMeta();

      final List<String> newLore = new ArrayList<>(im.getLore().size());
      im.getLore()
          .stream()
          .map(text -> ChatColor.translateAlternateColorCodes('&', text))
          .forEach(newLore::add);

      im.setLore(newLore);
      item.setItemMeta(im); */

      return this.item;
    }

    public int getLevel() {
      return level;
    }

    public int getBookshelves() {
      return bookshelves;
    }

    public BookEnchantment getEnchantment() {
      return enchantment;
    }
  }

  public static class BookEnchantment {

    private final Enchantment enchantment;
    private final int level;

    public BookEnchantment(Enchantment enchantment, int level) {
      this.enchantment = enchantment;
      this.level = level;
    }

    public static BookEnchantment getByString(String string) {
      final String[] array = string.split(";");
      final Enchantment enchantment = Enchantment.getByName(array[0]);
      final int level = Integer.parseInt(array[1]);
      return new BookEnchantment(enchantment == null ? Enchantment.ARROW_INFINITE : enchantment,
          level);
    }

    public Enchantment getEnchantment() {
      return enchantment;
    }

    public int getLevel() {
      return level;
    }
  }

}
