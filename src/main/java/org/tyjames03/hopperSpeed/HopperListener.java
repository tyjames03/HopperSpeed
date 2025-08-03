package org.tyjames03.hopperSpeed;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class HopperListener implements Listener {

    private final HopperSpeed plugin;

    public HopperListener(HopperSpeed plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        // Only override default if config is not vanilla
        boolean customDelay = plugin.getTransferIntervalTicks() != 8;
        boolean customAmount = plugin.getItemsPerTransfer() != 1;

        if (customDelay || customAmount) {
            event.setCancelled(true); // Cancel vanilla behavior

            Inventory source = event.getSource();         // Hopper inventory
            Inventory destination = event.getDestination(); // Where hopper is trying to send items

            new BukkitRunnable() {
                @Override
                public void run() {
                    int moved = 0;
                    for (int i = 0; i < source.getSize(); i++) {
                        if (moved >= plugin.getItemsPerTransfer()) break;
                        ItemStack stack = source.getItem(i);
                        if (stack != null && stack.getAmount() > 0) {
                            int amountToMove = Math.min(stack.getAmount(), plugin.getItemsPerTransfer() - moved);
                            ItemStack clone = stack.clone();
                            clone.setAmount(amountToMove);

                            var leftovers = destination.addItem(clone);
                            int actuallyMoved = amountToMove;
                            if (!leftovers.isEmpty()) {
                                int notMoved = leftovers.values().stream().mapToInt(ItemStack::getAmount).sum();
                                actuallyMoved = amountToMove - notMoved;
                            }
                            if (actuallyMoved > 0) {
                                stack.setAmount(stack.getAmount() - actuallyMoved);
                                moved += actuallyMoved;
                                if (stack.getAmount() == 0) source.setItem(i, null);
                            }
                        }
                    }
                }
            }.runTaskLater(plugin, plugin.getTransferIntervalTicks());
        }
        // Else: let vanilla happen
    }
}