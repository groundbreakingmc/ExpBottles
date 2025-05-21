package com.github.groundbreakingmc.expbottles.commands;

import com.github.groundbreakingmc.expbottles.ExpBottles;
import com.github.groundbreakingmc.expbottles.config.ConfigValues;
import com.github.groundbreakingmc.mylib.utils.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ExpCommand implements TabExecutor {

    private final ExpBottles plugin;
    private final ConfigValues.Settings settings;
    private final ConfigValues.Messages messages;

    public ExpCommand(ExpBottles plugin) {
        this.plugin = plugin;
        this.settings = plugin.getConfigValues().getSettings();
        this.messages = plugin.getConfigValues().getMessages();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (this.reload(args, sender)) {
            this.plugin.reloadConfig();
            sender.sendMessage(this.messages.getReloaded());
            return true;
        }

        boolean canOther = sender.hasPermission("expbottles.extended");
        if (!canOther && !sender.hasPermission("expbottles.use")) {
            sender.sendMessage(this.messages.getNoPermission());
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(canOther
                    ? this.messages.getUsageError()
                    : this.messages.getUsageErrorExtendedPerms()
            );
            return true;
        }

        int levels = this.getAmount(args[0]);
        if (levels < 1) {
            sender.sendMessage(this.messages.getAmountError());
            return true;
        }

        final Player target;
        if (sender instanceof final Player playerSender) {
            target = canOther && args.length > 1 ? Bukkit.getPlayer(args[1]) : playerSender;
        } else {
            if (args.length < 2) {
                sender.sendMessage(this.messages.getUsageErrorExtendedPerms());
                return true;
            }

            target = Bukkit.getPlayer(args[1]);
        }

        if (target == null) {
            sender.sendMessage(this.messages.getPlayerNotFound().replaceText(builder -> builder
                    .match("\\{player}")
                    .replacement(args[1])
            ));
            return true;
        }

        boolean force = args.length > 2 && args[2].equalsIgnoreCase("force");
        if (!force && this.settings.isRequireBottle() && this.processEmptyBottleCheck(target)) {
            if (sender != target) {
                sender.sendMessage(this.messages.getNoBottleIfOther().replaceText(builder -> builder
                        .match("\\{player}")
                        .replacement(target.getName())
                ));
            }

            target.sendMessage(this.messages.getNoBottle());
            return true;
        }

        final int expAmount = this.getExpAmount(levels);
        final int playerExp = target.getTotalExperience();
        sender.sendMessage(Integer.toString(expAmount));
        sender.sendMessage(Integer.toString(playerExp));
        if (!force && playerExp < expAmount) {
            if (sender != target) {
                sender.sendMessage(this.messages.getNotEnoughExpIfOther().replaceText(builder -> builder
                        .match("\\{player}")
                        .replacement(target.getName())
                ));
            }

            sender.sendMessage(this.messages.getNotEnoughExp());
            return true;
        }

        if (!force) {
            target.setExp(0.0F);
            target.setLevel(0);
            target.setTotalExperience(0);
            target.giveExp(playerExp - expAmount);
        }

        this.giveBottle(target, levels, expAmount);
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("expbottles.use")) {
            return List.of(args[0].isEmpty() ? "<количество>" : args[0]);
        }
        if (sender.hasPermission("expbottles.extended")) {
            if (args.length == 2) {
                return CommandUtils.tabCompletePlayerNames(args[1]);
            }
            if (args.length == 3) {
                return List.of("force");
            }
        }

        return List.of();
    }

    private boolean reload(String[] args, CommandSender sender) {
        return args.length > 0
                && args[0].equalsIgnoreCase("reload")
                && sender.hasPermission("expbottles.reload");
    }

    private int getAmount(final String input) {
        try {
            return Integer.parseInt(input);
        } catch (final NumberFormatException ex) {
            return -1;
        }
    }

    private int getExpAmount(int level) {
        if (level <= 15) {
            return level * level + 6 * level;
        } else if (level <= 30) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        }
    }

    private boolean processEmptyBottleCheck(Player player) {
        final PlayerInventory inventory = player.getInventory();
        return inventory.removeItem(new ItemStack(Material.GLASS_BOTTLE, 1)).isEmpty();
    }

    private void giveBottle(Player player, int levels, int expAmount) {
        ItemStack itemStack = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§c[★] §fПузырёк опыта §c[" + levels + " Ур.]");
        itemMeta.setLore(List.of("§7Содержит: " + levels + " Ур. опыта."));
        itemMeta.getPersistentDataContainer().set(ExpBottles.KEY, PersistentDataType.INTEGER, expAmount);

        itemStack.setItemMeta(itemMeta);

        final PlayerInventory inventory = player.getInventory();
        if (!inventory.addItem(itemStack).isEmpty()) {
            player.getWorld().dropItem(player.getLocation(), itemStack);
        }
    }
}
