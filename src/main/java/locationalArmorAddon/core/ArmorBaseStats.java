package locationalArmorAddon.core;

import locationalArmorAddon.util.PlayerUtils;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ArmorBaseStats {

    private static final PlayerUtils.ArmorStats ZERO = new PlayerUtils.ArmorStats(0, 0);
    private static final Map<String, PlayerUtils.ArmorStats> MAP = new HashMap<>();

    /*
        Spigot's API sadly doesn't allow us to retrieve the base stats of an
        armor piece, so we have to hard code them here for the time being...
    */

    static {
        // --- Leather ---
        MAP.put("LEATHER_HELMET", new PlayerUtils.ArmorStats(1, 0));
        MAP.put("LEATHER_CHESTPLATE", new PlayerUtils.ArmorStats(3, 0));
        MAP.put("LEATHER_LEGGINGS", new PlayerUtils.ArmorStats(2, 0));
        MAP.put("LEATHER_BOOTS", new PlayerUtils.ArmorStats(1, 0));

        // --- Copper ---
        MAP.put("COPPER_HELMET", new PlayerUtils.ArmorStats(2, 0));
        MAP.put("COPPER_CHESTPLATE", new PlayerUtils.ArmorStats(4, 0));
        MAP.put("COPPER_LEGGINGS", new PlayerUtils.ArmorStats(3, 0));
        MAP.put("COPPER_BOOTS", new PlayerUtils.ArmorStats(1, 0));

        // --- Golden ---
        MAP.put("GOLDEN_HELMET", new PlayerUtils.ArmorStats(2, 0));
        MAP.put("GOLDEN_CHESTPLATE", new PlayerUtils.ArmorStats(5, 0));
        MAP.put("GOLDEN_LEGGINGS", new PlayerUtils.ArmorStats(3, 0));
        MAP.put("GOLDEN_BOOTS", new PlayerUtils.ArmorStats(1, 0));

        // --- Chainmail ---
        MAP.put("CHAINMAIL_HELMET", new PlayerUtils.ArmorStats(2, 0));
        MAP.put("CHAINMAIL_CHESTPLATE", new PlayerUtils.ArmorStats(5, 0));
        MAP.put("CHAINMAIL_LEGGINGS", new PlayerUtils.ArmorStats(4, 0));
        MAP.put("CHAINMAIL_BOOTS", new PlayerUtils.ArmorStats(1, 0));

        // --- Iron ---
        MAP.put("IRON_HELMET", new PlayerUtils.ArmorStats(2, 0));
        MAP.put("IRON_CHESTPLATE", new PlayerUtils.ArmorStats(6, 0));
        MAP.put("IRON_LEGGINGS", new PlayerUtils.ArmorStats(5, 0));
        MAP.put("IRON_BOOTS", new PlayerUtils.ArmorStats(2, 0));

        // --- Diamond ---
        MAP.put("DIAMOND_HELMET", new PlayerUtils.ArmorStats(3, 2));
        MAP.put("DIAMOND_CHESTPLATE", new PlayerUtils.ArmorStats(8, 2));
        MAP.put("DIAMOND_LEGGINGS", new PlayerUtils.ArmorStats(6, 2));
        MAP.put("DIAMOND_BOOTS", new PlayerUtils.ArmorStats(3, 2));

        // --- Netherite ---
        MAP.put("NETHERITE_HELMET", new PlayerUtils.ArmorStats(3, 3));
        MAP.put("NETHERITE_CHESTPLATE", new PlayerUtils.ArmorStats(8, 3));
        MAP.put("NETHERITE_LEGGINGS", new PlayerUtils.ArmorStats(6, 3));
        MAP.put("NETHERITE_BOOTS", new PlayerUtils.ArmorStats(3, 3));

        // --- Turtle Shell ---
        MAP.put("TURTLE_HELMET", new PlayerUtils.ArmorStats(2, 0));
    }

    public static PlayerUtils.ArmorStats get(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return ZERO;
        return MAP.getOrDefault(stack.getType().name().toUpperCase(), ZERO);
    }

}
