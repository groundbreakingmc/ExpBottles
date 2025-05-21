package com.github.groundbreakingmc.expbottles.listeners;

import com.github.groundbreakingmc.expbottles.ExpBottles;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

public final class ExpBottleListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onThrow(final ProjectileLaunchEvent event) {
        final Projectile entity = event.getEntity();
        final ProjectileSource shooter = entity.getShooter();
        if (shooter instanceof final Player player) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack.getType() != Material.EXPERIENCE_BOTTLE) {
                itemStack = player.getInventory().getItemInOffHand();
                if (itemStack.getType() != Material.EXPERIENCE_BOTTLE) {
                    return;
                }
            }

            final Integer integer = itemStack.getItemMeta()
                    .getPersistentDataContainer()
                    .get(ExpBottles.KEY, PersistentDataType.INTEGER);

            if (integer != null) {
                entity.getPersistentDataContainer()
                        .set(ExpBottles.KEY, PersistentDataType.INTEGER, integer);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(final ExpBottleEvent event) {
        final Integer integer = event.getEntity()
                .getPersistentDataContainer()
                .get(ExpBottles.KEY, PersistentDataType.INTEGER);

        if (integer != null) {
            event.setExperience(integer);
        }
    }
}
