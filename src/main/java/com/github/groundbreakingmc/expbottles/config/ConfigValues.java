package com.github.groundbreakingmc.expbottles.config;

import com.github.groundbreakingmc.expbottles.ExpBottles;
import com.github.groundbreakingmc.mylib.config.ConfigProcessor;
import com.github.groundbreakingmc.mylib.config.annotations.Config;
import com.github.groundbreakingmc.mylib.config.annotations.Section;
import com.github.groundbreakingmc.mylib.config.annotations.Value;
import com.github.groundbreakingmc.mylib.logger.console.LoggerFactory;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.List;

@Getter
@Config(
        fileName = "config.yml",
        version = 1.0d,
        versionPath = "settings.config-version",
        colorizerPath = "settings.colorizer-mode"
)
public final class ConfigValues extends ConfigProcessor {

    @Section(name = "settings")
    private final Settings settings = new Settings();
    @Section(name = "messages")
    private final Messages messages = new Messages();

    public ConfigValues(ExpBottles plugin) {
        super(plugin, LoggerFactory.createLogger(plugin), false);
    }

    @Getter
    @SuppressWarnings("unused")
    public static class Settings {
        @Value(path = "command")
        private String command;
        @Value(path = "aliases")
        private List<String> aliases;
        @Value(path = "require-bottle")
        private boolean requireBottle;
    }

    @Getter
    @SuppressWarnings("unused")
    public static class Messages {
        @Value(path = "reload", colorize = true)
        private Component reloaded;
        @Value(path = "no-permission", colorize = true)
        private Component noPermission;
        @Value(path = "player-not-found", colorize = true)
        private Component playerNotFound;
        @Value(path = "usage-error", colorize = true)
        private Component usageError;
        @Value(path = "usage-error-if-has-extended-perms", colorize = true)
        private Component usageErrorExtendedPerms;
        @Value(path = "amount-error", colorize = true)
        private Component amountError;
        @Value(path = "no-bottle", colorize = true)
        private Component noBottle;
        @Value(path = "no-bottle-if-other", colorize = true)
        private Component noBottleIfOther;
        @Value(path = "not-enough", colorize = true)
        private Component notEnoughExp;
        @Value(path = "not-enough-if-other", colorize = true)
        private Component notEnoughExpIfOther;
    }
}
