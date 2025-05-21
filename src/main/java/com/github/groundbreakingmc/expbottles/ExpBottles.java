package com.github.groundbreakingmc.expbottles;

import com.github.groundbreakingmc.expbottles.commands.ExpCommand;
import com.github.groundbreakingmc.expbottles.config.ConfigValues;
import com.github.groundbreakingmc.expbottles.listeners.ExpBottleListener;
import com.github.groundbreakingmc.mylib.utils.command.CommandRuntimeUtils;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Set;

public final class ExpBottles extends JavaPlugin {

    public static final NamespacedKey KEY = new NamespacedKey("expbottles", "expbottles");

    @Getter
    private final ConfigValues configValues;
    private final ExpCommand expCommand;

    @Override
    public void onEnable() {
        this.reloadConfig();
        super.getServer().getPluginManager().registerEvents(new ExpBottleListener(), this);
    }

    @Override
    public void reloadConfig() {
        final ConfigValues.Settings settings = this.configValues.getSettings();
        String oldCommand = settings.getCommand();
        List<String> oldAliases = settings.getAliases();

        final ConfigurationNode config = this.configValues.setupValues();

        String newCommand = settings.getCommand();
        List<String> newAliases = settings.getAliases();

        if (!newCommand.equalsIgnoreCase(oldCommand)) {
            if (oldCommand != null) {
                CommandRuntimeUtils.unregisterCustomCommand(this, oldCommand);
            }

            CommandRuntimeUtils.register(this, newCommand, newAliases, this.expCommand);
        } else if (oldAliases != null && Set.of(oldAliases).equals(Set.of(newAliases))) {
            final PluginCommand command = CommandRuntimeUtils.getCustomCommand(this, oldCommand);
            command.setAliases(newAliases);
            CommandRuntimeUtils.syncCommands();
        }
    }

    public ExpBottles() {
        this.configValues = new ConfigValues(this);
        this.expCommand = new ExpCommand(this);
    }
}
