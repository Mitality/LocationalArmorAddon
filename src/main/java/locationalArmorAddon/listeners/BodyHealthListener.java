package locationalArmorAddon.listeners;

import bodyhealth.api.BodyHealthAPI;
import bodyhealth.api.events.BodyPartHealthChangeEvent;
import locationalArmorAddon.Main;
import locationalArmorAddon.config.Config;
import locationalArmorAddon.core.BodyPartDamage;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class BodyHealthListener implements Listener {

    @EventHandler
    public void onBodyPartHealthChange(BodyPartHealthChangeEvent event) {
        if (!Config.locational_armor_enabled) return;

        if (event.getOldHealth() < event.getNewHealth()) return;
        double damage = event.getOldHealth() - event.getNewHealth();

        Event underlyingEvent = event.getCause();
        if (!(underlyingEvent instanceof EntityDamageEvent trigger)) return;

        int breachLevel = getBreachLevel(trigger);

        DamageType damageType = trigger.getDamageSource().getDamageType();
        BodyPartDamage bodyPartDamage = new BodyPartDamage(damage, damageType, event.getPlayer(), event.getBodyPart(), breachLevel);

        String formula = Config.formula

            .replace("{base_damage}", String.valueOf(bodyPartDamage.BASE_DAMAGE))
            .replace("{pre_effect_damage}", String.valueOf(bodyPartDamage.PRE_EFFECT_DAMAGE))
            .replace("{pre_enchantment_damage}", String.valueOf(bodyPartDamage.PRE_ENCHANTMENT_DAMAGE))
            .replace("{pre_armor_damage}", String.valueOf(bodyPartDamage.PRE_ARMOR_DAMAGE))

            .replace("{total_armor_absorption}", String.valueOf(bodyPartDamage.TOTAL_ARMOR_ABSORPTION))
            .replace("{local_armor_absorption}", String.valueOf(bodyPartDamage.LOCAL_ARMOR_ABSORPTION))
            .replace("{total_enchantment_absorption}", String.valueOf(bodyPartDamage.TOTAL_ENCHANTMENT_ABSORPTION))
            .replace("{local_enchantment_absorption}", String.valueOf(bodyPartDamage.LOCAL_ENCHANTMENT_ABSORPTION))
            .replace("{effect_damage_absorption}", String.valueOf(bodyPartDamage.EFFECT_DAMAGE_ABSORPTION))

            .replace("{total_armor_reduction_ratio}", String.valueOf(bodyPartDamage.TOTAL_ARMOR_REDUCTION_RATIO))
            .replace("{local_armor_reduction_ratio}", String.valueOf(bodyPartDamage.LOCAL_ARMOR_REDUCTION_RATIO))
            .replace("{total_enchantment_reduction_ratio}", String.valueOf(bodyPartDamage.TOTAL_ENCHANTMENT_REDUCTION_RATIO))
            .replace("{local_enchantment_reduction_ratio}", String.valueOf(bodyPartDamage.LOCAL_ENCHANTMENT_REDUCTION_RATIO))
            .replace("{effect_damage_reduction_ratio}", String.valueOf(bodyPartDamage.EFFECT_DAMAGE_REDUCTION_RATIO));

        double newDamage = BodyHealthAPI.evaluateMathExpression(formula);

        Main.debug().logDev("Original Damage: " + damage);
        Main.debug().logDev("Modified Damage: " + newDamage);
        Main.debug().logDev("");

        if (newDamage == -1) return;

        event.setNewHealth(event.getOldHealth() - newDamage);
    }

    private static int getBreachLevel(EntityDamageEvent event) {

        if (!(event instanceof EntityDamageByEntityEvent edbe)) return 0;
        Entity damager = edbe.getDamager();
        if (!(damager instanceof LivingEntity living)) return 0;
        EntityEquipment equipment = living.getEquipment();
        if (equipment == null) return 0;
        ItemStack weapon = equipment.getItemInMainHand();
        if (weapon.getType().isAir()) return 0;

        return weapon.getEnchantmentLevel(Enchantment.BREACH);
    }

}
