package locationalArmorAddon.util;

import bodyhealth.config.Debug;
import bodyhealth.core.BodyPart;
import com.google.common.collect.Multimap;
import locationalArmorAddon.Main;
import locationalArmorAddon.config.Config;
import locationalArmorAddon.core.ArmorBaseStats;
import locationalArmorAddon.math.DamageCalculator;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerUtils {

    public record ArmorStats(
            double armorPoints,
            double armorToughness
    ) {}

    public static int getResistanceLevel(Player player) {

        PotionEffect effect = player.getPotionEffect(PotionEffectType.RESISTANCE);
        if (effect == null) return 0;

        // Potion levels are 0-based (e.g. level I = amplifier 0)
        return effect.getAmplifier() + 1;
    }

    public static DamageCalculator.EnchantmentLevels getTotalEnchantmentLevels(Player player) {

        int protection = 0;
        int blastProtection = 0;
        int fireProtection = 0;
        int projectileProtection = 0;
        int featherFalling = 0;

        for (ItemStack armorPiece : player.getInventory().getArmorContents()) {

            if (armorPiece == null || armorPiece.getType().isAir()) continue;
            Map<Enchantment, Integer> enchantments = armorPiece.getEnchantments();

            protection += enchantments.getOrDefault(Enchantment.PROTECTION, 0);
            blastProtection += enchantments.getOrDefault(Enchantment.BLAST_PROTECTION, 0);
            fireProtection += enchantments.getOrDefault(Enchantment.FIRE_PROTECTION, 0);
            projectileProtection += enchantments.getOrDefault(Enchantment.PROJECTILE_PROTECTION, 0);
            featherFalling += enchantments.getOrDefault(Enchantment.FEATHER_FALLING, 0);
        }

        Main.debug().logDev("Total Protection: " + protection);
        Main.debug().logDev("Total BlastProtection: " + blastProtection);
        Main.debug().logDev("Total FireProtection: " + fireProtection);
        Main.debug().logDev("Total ProjectileProtection: " + projectileProtection);
        Main.debug().logDev("Total FeatherFalling: " + featherFalling);

        return new DamageCalculator.EnchantmentLevels(
            protection,
            blastProtection,
            fireProtection,
            projectileProtection,
            featherFalling
        );
    }

    public static DamageCalculator.EnchantmentLevels getLocalEnchantmentLevels(Player player, BodyPart bodyPart) {

        EquipmentSlot localArmorPiece = switch (bodyPart) {
            case HEAD -> EquipmentSlot.HEAD;
            case BODY -> EquipmentSlot.CHEST;
            case ARM_LEFT, ARM_RIGHT -> Config.chestplate_protects_hands ? EquipmentSlot.CHEST : null;
            case LEG_LEFT, LEG_RIGHT -> EquipmentSlot.LEGS;
            case FOOT_LEFT, FOOT_RIGHT -> EquipmentSlot.FEET;
        };

        if (localArmorPiece == null) return new DamageCalculator.EnchantmentLevels(0,0,0,0,0);
        ItemStack armorPiece = player.getInventory().getItem(localArmorPiece);

        if (armorPiece == null || armorPiece.getType().isAir()) {
            return new DamageCalculator.EnchantmentLevels(0,0,0,0,0);
        }
        Map<Enchantment, Integer> enchantments = armorPiece.getEnchantments();

        Main.debug().logDev("Local Protection: " + enchantments.getOrDefault(Enchantment.PROTECTION, 0));
        Main.debug().logDev("Local BlastProtection: " + enchantments.getOrDefault(Enchantment.BLAST_PROTECTION, 0));
        Main.debug().logDev("Local FireProtection: " + enchantments.getOrDefault(Enchantment.FIRE_PROTECTION, 0));
        Main.debug().logDev("Local ProjectileProtection: " + enchantments.getOrDefault(Enchantment.PROJECTILE_PROTECTION, 0));
        Main.debug().logDev("Local FeatherFalling: " + enchantments.getOrDefault(Enchantment.FEATHER_FALLING, 0));

        return new DamageCalculator.EnchantmentLevels(
            enchantments.getOrDefault(Enchantment.PROTECTION, 0),
            enchantments.getOrDefault(Enchantment.BLAST_PROTECTION, 0),
            enchantments.getOrDefault(Enchantment.FIRE_PROTECTION, 0),
            enchantments.getOrDefault(Enchantment.PROJECTILE_PROTECTION, 0),
            enchantments.getOrDefault(Enchantment.FEATHER_FALLING, 0)
        );
    }

    public static ArmorStats getTotalArmorStats(Player player) {

        AttributeInstance armorAttribute = player.getAttribute(Attribute.ARMOR);
        AttributeInstance toughnessAttribute = player.getAttribute(Attribute.ARMOR_TOUGHNESS);

        double armor = armorAttribute != null ? armorAttribute.getValue() : 0;
        double toughness = toughnessAttribute != null ? toughnessAttribute.getValue() : 0;

        return new ArmorStats(armor, toughness);
    }

    public static ArmorStats getLocalArmorStats(Player player, BodyPart bodyPart) {

        EquipmentSlot slot = switch (bodyPart) {
            case HEAD -> EquipmentSlot.HEAD;
            case BODY -> EquipmentSlot.CHEST;
            case ARM_LEFT, ARM_RIGHT -> Config.chestplate_protects_hands ? EquipmentSlot.CHEST : null;
            case LEG_LEFT, LEG_RIGHT -> EquipmentSlot.LEGS;
            case FOOT_LEFT, FOOT_RIGHT -> EquipmentSlot.FEET;
        };

        if (slot == null) return new ArmorStats(0, 0);

        ItemStack item = player.getInventory().getItem(slot);
        if (item == null || item.getType().isAir()) return new ArmorStats(0, 0);
        ArmorStats baseStats = ArmorBaseStats.get(item);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return baseStats;

        Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers(slot);
        if (modifiers.isEmpty()) return baseStats;

        double armor = baseStats.armorPoints;
        double armorAddScalar = 0;
        List<Double> armorMultiplyScalar1 = new ArrayList<>();

        double toughness = baseStats.armorToughness;
        double toughnessAddScalar = 0;
        List<Double> toughnessMultiplyScalar1 = new ArrayList<>();

        for (AttributeModifier modifier : modifiers.get(Attribute.ARMOR)) {
            switch (modifier.getOperation()) {
                case ADD_NUMBER -> armor += modifier.getAmount();
                case ADD_SCALAR -> armorAddScalar += modifier.getAmount();
                case MULTIPLY_SCALAR_1 -> armorMultiplyScalar1.add(modifier.getAmount());
            }
        }

        for (AttributeModifier modifier : modifiers.get(Attribute.ARMOR_TOUGHNESS)) {
            switch (modifier.getOperation()) {
                case ADD_NUMBER -> toughness += modifier.getAmount();
                case ADD_SCALAR -> toughnessAddScalar += modifier.getAmount();
                case MULTIPLY_SCALAR_1 -> toughnessMultiplyScalar1.add(modifier.getAmount());
            }
        }

        armor *= 1 + armorAddScalar;
        for (double m : armorMultiplyScalar1) armor *= 1 + m;

        toughness *= 1 + toughnessAddScalar;
        for (double m : toughnessMultiplyScalar1) toughness *= 1 + m;

        return new ArmorStats(armor, toughness);
    }

}
