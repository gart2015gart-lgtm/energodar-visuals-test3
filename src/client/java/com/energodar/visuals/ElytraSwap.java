package com.energodar.visuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Меняет нагрудник на элитру и обратно одним нажатием. */
public final class ElytraSwap {
	private ElytraSwap() {}

	/** Слот нагрудника в меню инвентаря игрока. */
	private static final int CHEST_SLOT = 6;

	private static final Item[] CHESTPLATES = {
		Items.NETHERITE_CHESTPLATE,
		Items.DIAMOND_CHESTPLATE,
		Items.IRON_CHESTPLATE,
		Items.GOLDEN_CHESTPLATE,
		Items.CHAINMAIL_CHESTPLATE,
		Items.LEATHER_CHESTPLATE
	};

	public static void swap(Minecraft mc) {
		LocalPlayer p = mc.player;
		if (p == null || mc.gameMode == null || mc.screen != null) return;
		if (!p.containerMenu.getCarried().isEmpty()) return;

		boolean wearingElytra = p.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA);
		int from = wearingElytra ? findChestplate(p) : findElytra(p);

		if (from < 0) {
			p.displayClientMessage(Component.literal(wearingElytra
				? Text.tr("§cНет нагрудника в инвентаре", "§cNo chestplate in inventory")
				: Text.tr("§cНет целой элитры в инвентаре", "§cNo usable elytra in inventory")), true);
			return;
		}

		// Индекс инвентаря -> id слота в меню игрока (хотбар 0-8 -> 36-44, остальное 9-35 -> 9-35)
		int slot = from < 9 ? from + 36 : from;
		int sync = p.inventoryMenu.containerId;

		mc.gameMode.handleInventoryMouseClick(sync, slot, 0, ClickType.PICKUP, p);
		mc.gameMode.handleInventoryMouseClick(sync, CHEST_SLOT, 0, ClickType.PICKUP, p);
		mc.gameMode.handleInventoryMouseClick(sync, slot, 0, ClickType.PICKUP, p);

		p.displayClientMessage(Component.literal(wearingElytra
			? Text.tr("§aНагрудник надет", "§aChestplate equipped")
			: Text.tr("§aЭлитра надета", "§aElytra equipped")), true);
	}

	private static int findElytra(LocalPlayer p) {
		int best = -1;
		for (int i = 0; i < 36; i++) {
			ItemStack s = p.getInventory().getItem(i);
			if (s.is(Items.ELYTRA) && s.getDamageValue() < s.getMaxDamage() - 1) {
				best = i;
				break;
			}
		}
		return best;
	}

	private static int findChestplate(LocalPlayer p) {
		for (Item wanted : CHESTPLATES) {
			for (int i = 0; i < 36; i++) {
				if (p.getInventory().getItem(i).is(wanted)) return i;
			}
		}
		return -1;
	}
}
