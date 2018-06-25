package xyz.yooniks.enchants;

import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.yooniks.enchants.InventoryManager.BookEnchantment;
import xyz.yooniks.enchants.InventoryManager.BookItem;

public final class EnchantsPlugin extends JavaPlugin {

  private InventoryManager inventoryManager;

  @Override
  public void onEnable() {
    this.saveDefaultConfig();

    final String inventoryName = ChatColor.translateAlternateColorCodes('&',
        "&7Enchant GUI &c%bookshelves% bookshelves");
    final int inventorySize = 9 * 6;

    this.inventoryManager = new InventoryManager(inventoryName, inventorySize,
        this.getConfig().getConfigurationSection("bookItems").getKeys(false)
            .stream()
            /*.map(id -> {
             final ConfigurationSection section = this.getConfig().getConfigurationSection("bookItems." + id);

                return new BookItem(
                    section.getInt("item.slot"),
                    ItemUtil.fromSection(section.getConfigurationSection("item")),
                    section.getInt("cost.level"),
                    section.getInt("cost.bookshelves"),
                    BookEnchantment.getByString(section.getString("enchant")));
            }).collect(Collectors.toList())); */
            .map(id -> this.getConfig().getConfigurationSection("bookItems." + id))
            .map(section ->
                new BookItem(
                    section.getInt("item.slot"),
                    ItemUtil.fromSection(section.getConfigurationSection("item")),
                    section.getInt("cost.level"),
                    section.getInt("cost.bookshelves"),
                    BookEnchantment.getByString(section.getString("enchant")))
            ).collect(Collectors.toList()));

    this.getServer().getPluginManager().registerEvents(
        new InventoryListener(this.inventoryManager), this);
  }

  @Override
  public void onDisable() {
  }

}
