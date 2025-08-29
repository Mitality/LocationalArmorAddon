package locationalArmorAddon.util;

import bodyhealth.core.BodyPart;
import com.google.common.collect.Multimap;
import locationalArmorAddon.Main;
import locationalArmorAddon.config.Config;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        String key = switch (slot) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            default -> "";
        };

        Collection<AttributeModifier> baseArmorPointModifiers = Objects.requireNonNull(player.getAttribute(Attribute.ARMOR))
                .getModifiers().stream().filter(mod -> mod.getKey().getKey().equalsIgnoreCase("armor." + key)).toList();
        Collection<AttributeModifier> baseArmorToughnessModifiers = Objects.requireNonNull(player.getAttribute(Attribute.ARMOR_TOUGHNESS))
                .getModifiers().stream().filter(mod -> mod.getKey().getKey().equalsIgnoreCase("armor." + key)).toList();

        double baseArmorPoints = evaluateAttributeModifiers(baseArmorPointModifiers);
        double baseArmorToughness = evaluateAttributeModifiers(baseArmorToughnessModifiers);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return new ArmorStats(baseArmorPoints, baseArmorToughness);

        Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers(slot);
        if (modifiers.isEmpty()) new ArmorStats(baseArmorPoints, baseArmorToughness);

        Collection<AttributeModifier> metaArmorPointModifiers = modifiers.get(Attribute.ARMOR);
        Collection<AttributeModifier> metaArmorToughnessModifiers = modifiers.get(Attribute.ARMOR_TOUGHNESS);

        Collection<AttributeModifier> combinedArmorPointModifiers = Stream.concat(
                baseArmorPointModifiers.stream(),
                metaArmorPointModifiers.stream()
                        .filter(mod -> !mod.getKey().getKey().equalsIgnoreCase("armor." + key))
        ).collect(Collectors.toSet());

        Collection<AttributeModifier> combinedArmorToughnessModifiers = Stream.concat(
                baseArmorToughnessModifiers.stream(),
                metaArmorToughnessModifiers.stream()
                        .filter(mod -> !mod.getKey().getKey().equalsIgnoreCase("armor." + key))
        ).collect(Collectors.toSet());

        double combinedArmorPoints = evaluateAttributeModifiers(combinedArmorPointModifiers);
        double combinedArmorToughness = evaluateAttributeModifiers(combinedArmorToughnessModifiers);

        return new ArmorStats(combinedArmorPoints, combinedArmorToughness);
    }

    private static double evaluateAttributeModifiers(Collection<AttributeModifier> modifiers) {

        double value = 0;
        double addScalar = 0;
        List<Double> multiplyScalar1 = new ArrayList<>();

        for (AttributeModifier modifier : modifiers) {
            switch (modifier.getOperation()) {
                case ADD_NUMBER -> value += modifier.getAmount();
                case ADD_SCALAR -> addScalar += modifier.getAmount();
                case MULTIPLY_SCALAR_1 -> multiplyScalar1.add(modifier.getAmount());
            }
        }

        value *= 1 + addScalar;
        for (double m : multiplyScalar1) value *= 1 + m;
        return value;
    }

}
