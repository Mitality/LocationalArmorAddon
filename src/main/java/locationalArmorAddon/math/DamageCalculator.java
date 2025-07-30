package locationalArmorAddon.math;

import locationalArmorAddon.config.Config;
import locationalArmorAddon.util.PlayerUtils;
import org.bukkit.damage.DamageType;
import org.bukkit.tag.DamageTypeTags;

public class DamageCalculator {

    public record EnchantmentLevels(
        int protection,
        int blastProtection,
        int fireProtection,
        int projectileProtection,
        int featherFalling
    ) {}

    /**
     * Calculates the armor damage reduction of a given damage value based on given armor stats
     * @param originalDamage A damage value to calculate the reduced damage value for
     * @param damageType For what type of damage to calculate the reduced damage value for
     * @param stats Armor points and armor toughness stats of the player to calculate this for
     * @param breachLevel What level of the breach enchantment the source of the damage has
     * @return A reduction ratio (0.0 - 0.8) representing how much percent of incoming damage is absorbed
     */
    public static double getArmorDamageReductionRatio(double originalDamage, DamageType damageType, PlayerUtils.ArmorStats stats, int breachLevel) {
        if (DamageTypeTags.BYPASSES_ARMOR.isTagged(damageType)) return 0.0;

        // Cap armor stats
        double cappedArmorPoints = Math.min(30, stats.armorPoints());
        double cappedArmorToughness = Math.min(20, stats.armorToughness());

        // Compute intermediate values
        double damageFactor = (4 * originalDamage) / (cappedArmorToughness + 8);
        double effectiveReduction = Math.max(cappedArmorPoints / 5, cappedArmorPoints - damageFactor);
        double cappedReductionRatio = Math.min(20, effectiveReduction) / 25.0;

        // Apply breach enchantment
        return Math.max(0.0, cappedReductionRatio - (breachLevel * 0.15));
    }

    /**
     * Applies armor damage reduction to a given damage value based on given armor stats
     * @param originalDamage A damage value to calculate the reduced damage value for
     * @param damageType For what type of damage to calculate the reduced damage value for
     * @param stats Armor points and armor toughness stats of the player to calculate this for
     * @param breachLevel What level of the breach enchantment the source of the damage has
     * @return A damage value that has been appropriately reduced based on the given stats
     */
    public static double applyArmorDamageReduction(double originalDamage, DamageType damageType, PlayerUtils.ArmorStats stats, int breachLevel) {
        if (DamageTypeTags.BYPASSES_ARMOR.isTagged(damageType)) return originalDamage;

        // Calculate reduction ratio
        double finalReductionRatio = getArmorDamageReductionRatio(originalDamage, damageType, stats, breachLevel);

        // Reduce damage appropriately
        return originalDamage * (1.0 - finalReductionRatio);
    }

    /**
     * Uses binary search to estimate the original damage value before armor damage reduction was applied
     * @param reducedDamage A damage value to calculate the original, non-reduced, damage value for
     * @param damageType For what type of damage to calculate the original damage value for
     * @param stats Armor points and armor toughness stats of the player to calculate this for
     * @param breachLevel What level of the breach enchantment the source of the damage has
     * @return An estimate close to the original damage value before armor damage reduction was applied
     */
    public static double reverseArmorDamageReduction(double reducedDamage, DamageType damageType, PlayerUtils.ArmorStats stats, int breachLevel) {

        // Define bounds
        double low = reducedDamage;
        double high = reducedDamage * 5; // max armor reduction is 80%

        // Continue until close enough
        while (high - low > Config.binary_search_precision) {

            // Compute middle
            double mid = (low + high) / 2;
            double computed = applyArmorDamageReduction(mid, damageType, stats, breachLevel);

            // Move bounds accordingly
            if (computed > reducedDamage) {
                high = mid;
            } else {
                low = mid;
            }
        }

        double result = Math.round(((low + high) / 2) * 100.0) / 100.0;

        // Return estimate
        return result;
    }

    /**
     * Calculates how much incoming damage are absorbed by a given set of enchantments
     * @param damageType For what type of damage to calculate the reduction factor for
     * @param enchantmentLevels The levels of all enchantments that affect damage reduction
     * @return A reduction ratio (0.0 - 0.8) that represents how much percent of damage are absorbed
     */
    public static double getEnchantmentDamageReductionRatio(DamageType damageType, EnchantmentLevels enchantmentLevels) {
        double totalReduction = 0.0;

        if (!isExemptFromProtection(damageType)) {
            totalReduction += enchantmentLevels.protection * 0.04;
        }

        // Blast Protection (8% per level)
        if (DamageTypeTags.IS_EXPLOSION.isTagged(damageType)) {
            totalReduction += enchantmentLevels.blastProtection * 0.08;
        }

        // Fire Protection (8% per level)
        if (DamageTypeTags.IS_FIRE.isTagged(damageType)) {
            totalReduction += enchantmentLevels.fireProtection * 0.08;
        }

        // Projectile Protection (8% per level)
        if (DamageTypeTags.IS_PROJECTILE.isTagged(damageType)) {
            totalReduction += enchantmentLevels.projectileProtection * 0.08;
        }

        // Feather Falling (12% per level)
        if (DamageTypeTags.IS_FALL.isTagged(damageType)) {
            totalReduction += enchantmentLevels.featherFalling * 0.12;
        }

        // Cap total reduction at 80%
        totalReduction = Math.min(totalReduction, 0.80);

        return totalReduction;
    }

    /**
     * Applies enchantment damage reduction to a given damage value based on given enchantments
     * @param originalDamage A damage value to calculate the reduced damage value for
     * @param damageType For what type of damage to calculate the reduced damage value for
     * @param enchantmentLevels The levels of all enchantments that affect damage reduction
     * @return A damage value that has been appropriately reduced based on the given stats
     */
    public static double applyEnchantmentDamageReduction(double originalDamage, DamageType damageType, EnchantmentLevels enchantmentLevels) {

        if (DamageTypeTags.BYPASSES_ENCHANTMENTS.isTagged(damageType)) return originalDamage;

        // Calculate reduction ratio
        double reductionRatio = getEnchantmentDamageReductionRatio(damageType, enchantmentLevels);

        // Apply damage reduction
        return originalDamage * (1.0 - reductionRatio);
    }


    /**
     * Reverses the enchantment damage reduction of a given damage value based on given enchantments
     * @param reducedDamage A damage value to calculate the original damage value for
     * @param damageType For what type of damage to calculate the original damage value for
     * @param enchantmentLevels The levels of all enchantments that affect damage reduction
     * @return A damage value that has been appropriately increased based on the given stats
     */
    public static double reverseEnchantmentDamageReduction(double reducedDamage, DamageType damageType, EnchantmentLevels enchantmentLevels) {

        if (DamageTypeTags.BYPASSES_ENCHANTMENTS.isTagged(damageType)) return reducedDamage;
        double reductionRatio = getEnchantmentDamageReductionRatio(damageType, enchantmentLevels);

        // Reverse damage reduction
        return reducedDamage / (1.0 - reductionRatio);
    }

    /**
     * Helper method to check whether a given DamageType is affected by the protection enchantment
     * @param damageType The DamageType to check this for
     * @return true if unaffected by protection
     */
    public static boolean isExemptFromProtection(DamageType damageType) {
        return damageType == DamageType.STARVE ||
                damageType == DamageType.SONIC_BOOM ||
                damageType == DamageType.OUT_OF_WORLD ||
                damageType == DamageType.GENERIC_KILL;
    }

    /**
     * Calculates the effect damage reduction to a given damage value based on a given resistance effect level
     * @param damageType For what type of damage to calculate the reduced damage value for
     * @param resistanceLevel What level of the resistance effect the player currently has
     * @return A reduction ratio (0.0 - 1.0) representing how much incoming damage is absorbed
     */
    public static double getEffectDamageReductionRatio(DamageType damageType, int resistanceLevel) {
        if (DamageTypeTags.BYPASSES_EFFECTS.isTagged(damageType)) return 0.0;
        double totalReduction = 0.0;

        // Resistance (20% per level)
        if (!DamageTypeTags.BYPASSES_RESISTANCE.isTagged(damageType)) {
            totalReduction += resistanceLevel * 0.2;
        }

        return totalReduction;
    }

    /**
     * Applies effect damage reduction to a given damage value based on a given resistance effect level
     * @param originalDamage A damage value to calculate the reduced damage value for
     * @param damageType For what type of damage to calculate the reduced damage value for
     * @param resistanceLevel What level of the resistance effect the player currently has
     * @return A damage value that has been appropriately reduced based on the given stats
     */
    public static double applyEffectDamageReduction(double originalDamage, DamageType damageType, int resistanceLevel) {

        if (DamageTypeTags.BYPASSES_EFFECTS.isTagged(damageType)) return originalDamage;

        // Calculate reduction ratio
        double reductionRatio = getEffectDamageReductionRatio(damageType, resistanceLevel);

        // Apply damage reduction
        return originalDamage * (1.0 - reductionRatio);
    }

    /**
     * Reverses the effect damage reduction of a given damage value based on a given resistance effect level
     * @param originalDamage A damage value to calculate the original damage value for
     * @param damageType For what type of damage to calculate the original damage value for
     * @param resistanceLevel What level of the resistance effect the player currently has
     * @return A damage value that has been appropriately increased based on the given stats
     */
    public static double reverseEffectDamageReduction(double originalDamage, DamageType damageType, int resistanceLevel) {

        if (DamageTypeTags.BYPASSES_EFFECTS.isTagged(damageType)) return originalDamage;

        // Calculate reduction ratio
        double reductionRatio = getEffectDamageReductionRatio(damageType, resistanceLevel);

        // Reverse damage reduction
        return originalDamage / (1.0 - reductionRatio);
    }

}
