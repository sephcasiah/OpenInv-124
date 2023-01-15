/*
 * Copyright (C) 2011-2022 lishid. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.lishid.openinv;

import com.lishid.openinv.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

record PlayerListener(OpenInv plugin) implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        plugin.setPlayerOnline(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        plugin.setPlayerOffline(event.getPlayer());
    }

    @EventHandler
    private void onWorldChange(@NotNull PlayerChangedWorldEvent event) {
        plugin.changeWorld(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerInteract(@NotNull PlayerInteractEvent event) {

        // Do not cancel 3rd party plugins' custom events
        if (!PlayerInteractEvent.class.equals(event.getClass())) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getPlayer().isSneaking()
                || event.useInteractedBlock() == Result.DENY || event.getClickedBlock() == null
                || !plugin.getAnySilentContainer().isAnySilentContainer(event.getClickedBlock())) {
            return;
        }

        Player player = event.getPlayer();
        boolean any = Permissions.ANYCHEST.hasPermission(player) && plugin.getAnyContainerStatus(player);
        boolean needsAny = plugin.getAnySilentContainer().isAnyContainerNeeded(event.getClickedBlock());

        if (!any && needsAny) {
            return;
        }

        boolean silent = Permissions.SILENT.hasPermission(player) && plugin.getSilentContainerStatus(player);

        // If anycontainer or silentcontainer is active
        if (any || silent) {
            if (plugin.getAnySilentContainer().activateContainer(player, silent, event.getClickedBlock())) {
                if (silent && needsAny) {
                    plugin.sendSystemMessage(player, "messages.info.containerBlockedSilent");
                } else if (needsAny) {
                    plugin.sendSystemMessage(player, "messages.info.containerBlocked");
                } else if (silent) {
                    plugin.sendSystemMessage(player, "messages.info.containerSilent");
                }
            }
            event.setCancelled(true);
        }
    }

}
