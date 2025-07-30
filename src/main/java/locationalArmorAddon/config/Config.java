package locationalArmorAddon.config;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static boolean locational_armor_enabled;
    public static boolean chestplate_protects_hands;
    public static double binary_search_precision;
    public static String formula;

    public static void load(FileConfiguration config) {

        locational_armor_enabled = config.getBoolean("locational-armor", true);
        chestplate_protects_hands = config.getBoolean("chestplate-protects-hands", true);
        binary_search_precision = config.getDouble("binary-search-precision", 0.001);
        formula = config.getString("formula", "{base_damage}");

    }

}
