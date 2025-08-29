package locationalArmorAddon.core;

import bodyhealth.core.BodyPart;
import locationalArmorAddon.Main;
import locationalArmorAddon.math.DamageCalculator;
import locationalArmorAddon.util.PlayerUtils;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;

public class BodyPartDamage {

    public DamageType DAMAGE_TYPE;

    public double BASE_DAMAGE;
    public double PRE_EFFECT_DAMAGE;
    public double PRE_ENCHANTMENT_DAMAGE;
    public double PRE_ARMOR_DAMAGE;

    public double EFFECT_DAMAGE_ABSORPTION;
    public double LOCAL_ENCHANTMENT_ABSORPTION;
    public double TOTAL_ENCHANTMENT_ABSORPTION;
    public double LOCAL_ARMOR_ABSORPTION;
    public double TOTAL_ARMOR_ABSORPTION;

    public double EFFECT_DAMAGE_REDUCTION_RATIO;
    public double LOCAL_ENCHANTMENT_REDUCTION_RATIO;
    public double TOTAL_ENCHANTMENT_REDUCTION_RATIO;
    public double LOCAL_ARMOR_REDUCTION_RATIO;
    public double TOTAL_ARMOR_REDUCTION_RATIO;

    public BodyPartDamage(double baseDamage, DamageType damageType, Player player, BodyPart bodyPart, int breachLevel) {

        Main.debug().logDev("");
        Main.debug().logDev(player.getName() + "'s " + bodyPart.name() + " received " + baseDamage + " damage (type " + damageType.getKey().getKey().toUpperCase()  + ") with breach level " + breachLevel);

        int resistanceLevel = PlayerUtils.getResistanceLevel(player);
        Main.debug().logDev("Resistance effect level: " + resistanceLevel);

        DamageCalculator.EnchantmentLevels totalEnchantmentLevels = PlayerUtils.getTotalEnchantmentLevels(player);
        DamageCalculator.EnchantmentLevels localEnchantmentLevels = PlayerUtils.getLocalEnchantmentLevels(player, bodyPart);

        PlayerUtils.ArmorStats totalArmorStats = PlayerUtils.getTotalArmorStats(player);
        PlayerUtils.ArmorStats localArmorStats = PlayerUtils.getLocalArmorStats(player, bodyPart);

        Main.debug().logDev("Total Armor Points: " + totalArmorStats.armorPoints());
        Main.debug().logDev("Total Armor Toughness: " + totalArmorStats.armorToughness());
        Main.debug().logDev("Local Armor Points: " + localArmorStats.armorPoints());
        Main.debug().logDev("Local Armor Toughness: " + localArmorStats.armorToughness());

        DAMAGE_TYPE = damageType;
        BASE_DAMAGE = baseDamage;

        PRE_EFFECT_DAMAGE = DamageCalculator.reverseEffectDamageReduction(BASE_DAMAGE, DAMAGE_TYPE, resistanceLevel);
        EFFECT_DAMAGE_ABSORPTION = PRE_EFFECT_DAMAGE - BASE_DAMAGE;
        EFFECT_DAMAGE_REDUCTION_RATIO = DamageCalculator.getEffectDamageReductionRatio(DAMAGE_TYPE, resistanceLevel);

        PRE_ENCHANTMENT_DAMAGE = DamageCalculator.reverseEnchantmentDamageReduction(PRE_EFFECT_DAMAGE, DAMAGE_TYPE, totalEnchantmentLevels);
        TOTAL_ENCHANTMENT_ABSORPTION = PRE_ENCHANTMENT_DAMAGE - PRE_EFFECT_DAMAGE;
        TOTAL_ENCHANTMENT_REDUCTION_RATIO = DamageCalculator.getEnchantmentDamageReductionRatio(DAMAGE_TYPE, totalEnchantmentLevels);
        LOCAL_ENCHANTMENT_REDUCTION_RATIO = DamageCalculator.getEnchantmentDamageReductionRatio(DAMAGE_TYPE, localEnchantmentLevels);
        LOCAL_ENCHANTMENT_ABSORPTION = PRE_ENCHANTMENT_DAMAGE - DamageCalculator.applyEnchantmentDamageReduction(PRE_ENCHANTMENT_DAMAGE, DAMAGE_TYPE, localEnchantmentLevels);

        PRE_ARMOR_DAMAGE = DamageCalculator.reverseArmorDamageReduction(PRE_ENCHANTMENT_DAMAGE, DAMAGE_TYPE, totalArmorStats, breachLevel);
        TOTAL_ARMOR_ABSORPTION = PRE_ARMOR_DAMAGE - PRE_ENCHANTMENT_DAMAGE;
        TOTAL_ARMOR_REDUCTION_RATIO = DamageCalculator.getArmorDamageReductionRatio(PRE_ARMOR_DAMAGE, DAMAGE_TYPE, totalArmorStats, breachLevel);
        LOCAL_ARMOR_REDUCTION_RATIO = DamageCalculator.getArmorDamageReductionRatio(PRE_ARMOR_DAMAGE, DAMAGE_TYPE, localArmorStats, breachLevel);
        LOCAL_ARMOR_ABSORPTION = PRE_ARMOR_DAMAGE - DamageCalculator.applyArmorDamageReduction(PRE_ARMOR_DAMAGE, DAMAGE_TYPE, localArmorStats, breachLevel);

        Main.debug().logDev("");
        Main.debug().logDev("BASE_DAMAGE: " + BASE_DAMAGE);
        Main.debug().logDev("PRE_EFFECT_DAMAGE: " + PRE_EFFECT_DAMAGE);
        Main.debug().logDev("EFFECT_DAMAGE_ABSORPTION: " + EFFECT_DAMAGE_ABSORPTION);
        Main.debug().logDev("EFFECT_DAMAGE_REDUCTION_RATIO: " + EFFECT_DAMAGE_REDUCTION_RATIO);
        Main.debug().logDev("PRE_ENCHANTMENT_DAMAGE: " + PRE_ENCHANTMENT_DAMAGE);
        Main.debug().logDev("TOTAL_ENCHANTMENT_ABSORPTION: " + TOTAL_ENCHANTMENT_ABSORPTION);
        Main.debug().logDev("TOTAL_ENCHANTMENT_REDUCTION_RATIO: " + TOTAL_ENCHANTMENT_REDUCTION_RATIO);
        Main.debug().logDev("LOCAL_ENCHANTMENT_REDUCTION_RATIO: " + LOCAL_ENCHANTMENT_REDUCTION_RATIO);
        Main.debug().logDev("LOCAL_ENCHANTMENT_ABSORPTION: " + LOCAL_ENCHANTMENT_ABSORPTION);
        Main.debug().logDev("PRE_ARMOR_DAMAGE: " + PRE_ARMOR_DAMAGE);
        Main.debug().logDev("TOTAL_ARMOR_ABSORPTION: " + TOTAL_ARMOR_ABSORPTION);
        Main.debug().logDev("TOTAL_ARMOR_REDUCTION_RATIO: " + TOTAL_ARMOR_REDUCTION_RATIO);
        Main.debug().logDev("LOCAL_ARMOR_REDUCTION_RATIO: " + LOCAL_ARMOR_REDUCTION_RATIO);
        Main.debug().logDev("LOCAL_ARMOR_ABSORPTION: " + LOCAL_ARMOR_ABSORPTION);
        Main.debug().logDev("");

    }

}
