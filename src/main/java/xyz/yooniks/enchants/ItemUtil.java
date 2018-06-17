package xyz.yooniks.enchants;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemUtil {

  private ItemUtil() {
  }

  public static ItemStack fromSection(ConfigurationSection section) {
    final ItemStack item = new ItemStack(Material.GRASS);
    final ItemMeta meta = item.getItemMeta();

    if (section.isString("material")) {
      final Material material = Material.getMaterial(section.getString("material"));
      if (material != null) {
        item.setType(material);
      }
    }
    if (section.isList("lore")) {
      final List<String> current_lore = section.getStringList("lore");
      final List<String> lore = new ArrayList<>(current_lore.size());

      current_lore
          .forEach(string ->
              lore.add(ChatColor.translateAlternateColorCodes('&', string)));

      meta.setLore(lore);
    }
    if (section.isString("name")) {
      meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name")));
    }
    if (section.isInt("amount")) {
      item.setAmount(section.getInt("amount"));
    }
    if (section.isInt("data") || section.isInt("durability")) {
      item.setDurability((short) section.getInt("data", section.getInt("durability")));
    }

    item.setItemMeta(meta);
    return item;
  }

}
