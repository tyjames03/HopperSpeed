package org.tyjames03.hopperSpeed;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class HopperSpeed extends JavaPlugin {

    private int transferIntervalTicks;
    private int itemsPerTransfer;

    @Override
    public void onEnable() {
        // Ensure default config is generated and visible in plugins/HopperSpeed/config.yml
        saveDefaultConfig();
        loadConfig();
        getServer().getPluginManager().registerEvents(new HopperListener(this), this);
        getLogger().info("HopperSpeed enabled! Interval: " + transferIntervalTicks + " ticks, Items per transfer: " + itemsPerTransfer);
    }

    @Override
    public void onDisable() {
        getLogger().info("HopperSpeed disabled.");
    }

    public void loadConfig() {
        FileConfiguration config = getConfig();
        this.transferIntervalTicks = config.getInt("hopper-transfer-interval-ticks", 8);
        this.itemsPerTransfer = config.getInt("hopper-items-per-transfer", 1);
    }

    public int getTransferIntervalTicks() {
        return transferIntervalTicks;
    }

    public int getItemsPerTransfer() {
        return itemsPerTransfer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("hspeed")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("hopperspeed.reload")) {
                    sender.sendMessage("§cYou do not have permission to use this command.");
                    return true;
                }
                reloadConfig();
                loadConfig();
                sender.sendMessage("§a[HopperSpeed] Configuration reloaded!");
                getLogger().info("Configuration reloaded by " + sender.getName());
                return true;
            }
            sender.sendMessage("§cUsage: /hspeed reload");
            return true;
        }
        return false;
    }
}