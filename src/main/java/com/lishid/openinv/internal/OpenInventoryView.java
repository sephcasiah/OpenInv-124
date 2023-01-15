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

package com.lishid.openinv.internal;

import com.lishid.openinv.OpenInv;
import com.lishid.openinv.util.lang.Replacement;
import java.util.Objects;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public class OpenInventoryView extends InventoryView {

    private final @NotNull Player player;
    private final @NotNull ISpecialInventory inventory;
    private final @NotNull String titleKey;
    private final @NotNull String titleDefaultSuffix;
    private String title;

    public OpenInventoryView(
            @NotNull Player player,
            @NotNull ISpecialInventory inventory,
            @NotNull String titleKey,
            @NotNull String titleDefaultSuffix) {
        this.player = player;
        this.inventory = inventory;
        this.titleKey = titleKey;
        this.titleDefaultSuffix = titleDefaultSuffix;
    }

    @Override
    public @NotNull Inventory getTopInventory() {
        return inventory.getBukkitInventory();
    }

    @Override
    public @NotNull Inventory getBottomInventory() {
        return getPlayer().getInventory();
    }

    @Override
    public @NotNull HumanEntity getPlayer() {
        return player;
    }

    @Override
    public @NotNull InventoryType getType() {
        return inventory.getBukkitInventory().getType();
    }

    @Override
    public @NotNull String getTitle() {
        if (title == null) {
            HumanEntity owner = inventory.getPlayer();

            String localTitle = OpenInv.getPlugin(OpenInv.class)
                    .getLocalizedMessage(
                            player,
                            titleKey,
                            new Replacement("%player%", owner.getName()));
            title = Objects.requireNonNullElseGet(localTitle, () -> owner.getName() + titleDefaultSuffix);
        }

        return title;
    }

}
