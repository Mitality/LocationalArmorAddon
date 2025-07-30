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

        DAMAGE_TYPE = damageType;
        BASE_DAMAGE = baseDamage;

        PRE_EFFECT_DAMAGE = DamageCalculator.reverseEffectDamageReduction(BASE_DAMAGE, DAMAGE_TYPE, PlayerUtils.getResistanceLevel(player));
        EFFECT_DAMAGE_ABSORPTION = PRE_EFFECT_DAMAGE - BASE_DAMAGE;
        EFFECT_DAMAGE_REDUCTION_RATIO = DamageCalculator.getEffectDamageReductionRatio(DAMAGE_TYPE, PlayerUtils.getResistanceLevel(player));

        PRE_ENCHANTMENT_DAMAGE = DamageCalculator.reverseEnchantmentDamageReduction(PRE_EFFECT_DAMAGE, DAMAGE_TYPE, PlayerUtils.getTotalEnchantmentLevels(player));
        TOTAL_ENCHANTMENT_ABSORPTION = PRE_ENCHANTMENT_DAMAGE - PRE_EFFECT_DAMAGE;
        TOTAL_ENCHANTMENT_REDUCTION_RATIO = DamageCalculator.getEnchantmentDamageReductionRatio(DAMAGE_TYPE, PlayerUtils.getTotalEnchantmentLevels(player));
        LOCAL_ENCHANTMENT_REDUCTION_RATIO = DamageCalculator.getEnchantmentDamageReductionRatio(DAMAGE_TYPE, PlayerUtils.getLocalEnchantmentLevels(player, bodyPart));
        LOCAL_ENCHANTMENT_ABSORPTION = PRE_ENCHANTMENT_DAMAGE - DamageCalculator.applyEnchantmentDamageReduction(PRE_ENCHANTMENT_DAMAGE, DAMAGE_TYPE, PlayerUtils.getLocalEnchantmentLevels(player, bodyPart));

        PRE_ARMOR_DAMAGE = DamageCalculator.reverseArmorDamageReduction(PRE_ENCHANTMENT_DAMAGE, DAMAGE_TYPE, PlayerUtils.getTotalArmorStats(player), breachLevel);
        TOTAL_ARMOR_ABSORPTION = PRE_ARMOR_DAMAGE - PRE_ENCHANTMENT_DAMAGE;
        TOTAL_ARMOR_REDUCTION_RATIO = DamageCalculator.getArmorDamageReductionRatio(PRE_ARMOR_DAMAGE, DAMAGE_TYPE, PlayerUtils.getTotalArmorStats(player), breachLevel);
        LOCAL_ARMOR_REDUCTION_RATIO = DamageCalculator.getArmorDamageReductionRatio(PRE_ARMOR_DAMAGE, DAMAGE_TYPE, PlayerUtils.getLocalArmorStats(player, bodyPart), breachLevel);
        LOCAL_ARMOR_ABSORPTION = PRE_ARMOR_DAMAGE - DamageCalculator.applyArmorDamageReduction(PRE_ARMOR_DAMAGE, DAMAGE_TYPE, PlayerUtils.getLocalArmorStats(player, bodyPart), breachLevel);

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
