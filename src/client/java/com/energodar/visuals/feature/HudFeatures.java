package com.energodar.visuals.feature;

import com.energodar.visuals.HudCtx;
import com.energodar.visuals.Notifier;
import com.energodar.visuals.Targets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Все модули категории HUD. */
public final class HudFeatures {
	private HudFeatures() {}

	// ------------------------------------------------------------------ Ватермарк
	public static final class Watermark extends Feature {
		private final Setting.Bool fps = add(new Setting.Bool("fps", "FPS и пинг", "FPS and ping", true));

		public Watermark() {
			super("watermark", "Ватермарк", "Watermark", Category.HUD, true);
		}

		@Override
		public void renderHud(GuiGraphics g, HudCtx c) {
			String name = "Energodar Visuals";
			String info = fps.value ? c.mc.getFps() + " FPS  " + c.ping() + " ms" : "";
			int nameW = c.font.width(name);
			int total = nameW + 10 + (info.isEmpty() ? 0 : 6 + c.font.width(info));
			c.panel(g, 5, c.leftY, total, 14);
			g.drawString(c.font, name, 11, c.leftY + 3, c.accent, true);
			if (!info.isEmpty()) g.drawString(c.font, info, 11 + nameW + 6, c.leftY + 3, 0xFFFFFFFF, true);
			c.leftY += 18;
		}
	}

	// ------------------------------------------------------------------ Координаты
	public static final class Coordinates extends Feature {
		public Coordinates() {
			super("coords", "Координаты", "Coordinates", Category.HUD, true);
		}

		@Override
		public void renderHud(GuiGraphics g, HudCtx c) {
			LocalPlayer p = c.p;
			String s = "XYZ  " + p.getBlockX() + "  " + p.getBlockY() + "  " + p.getBlockZ();
			c.panel(g, 5, c.leftY, c.font.width(s) + 10, 14);
			g.drawString(c.font, s, 11, c.leftY + 3, 0xFFFFFFFF, true);
			c.leftY += 18;
		}
	}

	// ------------------------------------------------------------------ Скорость и время
	public static final class Info extends Feature {
		private final Setting.Bool bps = add(new Setting.Bool("bps", "Скорость (б/с)", "Speed (b/s)", true));
		private final Setting.Bool time = add(new Setting.Bool("time", "Время", "Clock", true));
		private double lastX;
		private double lastZ;
		private double speed;

		public Info() {
			super("info", "Инфо", "Info", Category.HUD, true);
		}

		@Override
		public void tick(Minecraft mc) {
			LocalPlayer p = mc.player;
			if (p == null) return;
			speed = Math.hypot(p.getX() - lastX, p.getZ() - lastZ) * 20.0;
			lastX = p.getX();
			lastZ = p.getZ();
		}

		@Override
		public void renderHud(GuiGraphics g, HudCtx c) {
			StringBuilder sb = new StringBuilder();
			if (bps.value) sb.append(String.format(Locale.ROOT, "BPS %.1f", speed));
			if (time.value) {
				if (sb.length() > 0) sb.append("  ");
				sb.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
			}
			if (sb.length() == 0) return;
			String s = sb.toString();
			c.panel(g, 5, c.leftY, c.font.width(s) + 10, 14);
			g.drawString(c.font, s, 11, c.leftY + 3, 0xFFFFFFFF, true);
			c.leftY += 18;
		}
	}

	// ------------------------------------------------------------------ Броня
	private static final EquipmentSlot[] ARMOR = {
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};

	public static final class ArmorHud extends Feature {
		private final Setting.Bool percent = add(new Setting.Bool("percent", "Проценты", "Percent", true));

		public ArmorHud() {
			super("armor", "Броня", "Armor", Category.HUD, true);
		}

		@Override
		public void renderHud(GuiGraphics g, HudCtx c) {
			int ay = c.h / 2 - 36;
			for (EquipmentSlot slot : ARMOR) {
				ItemStack st = c.p.getItemBySlot(slot);
				if (st.isEmpty()) continue;
				g.renderItem(st, 6, ay);
				if (percent.value && st.isDamageableItem()) {
					int left = st.getMaxDamage() - st.getDamageValue();
					int pct = Mth.clamp(left * 100 / Math.max(1, st.getMaxDamage()), 0, 100);
					g.drawString(c.font, pct + "%", 26, ay + 4, 0xFF000000 | st.getBarColor(), true);
				}
				ay += 18;
			}
		}
	}

	// ------------------------------------------------------------------ Таргет HUD
	public static final class TargetHud extends Feature {
		public TargetHud() {
			super("targethud", "Таргет HUD", "Target HUD", Category.HUD, true);
		}

		@Override
		public void renderHud(GuiGraphics g, HudCtx c) {
			LivingEntity t = Targets.last;
			if (t == null) return;
			if (t.isRemoved() || !t.isAlive() || System.currentTimeMillis() - Targets.time > 5000) {
				return;
			}
			int bx = c.w / 2 + 24;
			int by = c.h / 2 + 10;
			int bw = 112;
			int bh = 38;
			c.panel(g, bx, by, bw, bh);
			g.drawString(c.font, t.getName().getString(), bx + 7, by + 5, 0xFFFFFFFF, true);

			float hp = t.getHealth();
			float max = Math.max(1f, t.getMaxHealth());
			float ratio = Mth.clamp(hp / max, 0f, 1f);
			int barX = bx + 7;
			int barW = bw - 14;
			g.fill(barX, by + 19, barX + barW, by + 24, 0xFF303030);
			g.fill(barX, by + 19, barX + (int) (barW * ratio), by + 24, c.accent);
			g.drawString(c.font, String.format(Locale.ROOT, "%.1f / %.1f HP", hp, max), barX, by + 27, 0xFFDDDDDD, true);
		}
	}

	// ------------------------------------------------------------------ Список модулей
	public static final class ModuleList extends Feature {
		public ModuleList() {
			super("modulelist", "Список модулей", "Module list", Category.HUD, true);
		}

		@Override
		public void renderHud(GuiGraphics g, HudCtx c) {
			List<Feature> on = new ArrayList<>();
			for (Feature f : Features.ALL) {
				if (f.enabled && f.category != Category.HUD && f.category != Category.CLIENT) on.add(f);
			}
			Font font = c.font;
			on.sort(Comparator.comparingInt((Feature f) -> font.width(f.name())).reversed());
			for (Feature f : on) {
				String s = f.name();
				int tw = font.width(s);
				int x = c.w - 5 - tw;
				g.fill(x - 4, c.rightY, c.w - 3, c.rightY + 11, 0x90000000);
				g.fill(c.w - 5 + 2, c.rightY, c.w - 3, c.rightY + 11, c.accent);
				g.drawString(font, s, x, c.rightY + 2, 0xFFFFFFFF, true);
				c.rightY += 12;
			}
			c.rightY += 4;
		}
	}

	// ------------------------------------------------------------------ Эффекты зелий
	public static final class PotionsHud extends Feature {
		public PotionsHud() {
			super("potions", "Эффекты", "Potions", Category.HUD, true);
		}

		@Override
		public void renderHud(GuiGraphics g, HudCtx c) {
			for (MobEffectInstance e : c.p.getActiveEffects()) {
				String name = e.getEffect().value().getDisplayName().getString();
				if (e.getAmplifier() > 0) name += " " + (e.getAmplifier() + 1);
				String time;
				if (e.isInfiniteDuration()) {
					time = "inf";
				} else {
					int sec = e.getDuration() / 20;
					time = String.format(Locale.ROOT, "%d:%02d", sec / 60, sec % 60);
				}
				String s = name + "  " + time;
				int color = e.getEffect().value().isBeneficial() ? 0xFF8CFF8C : 0xFFFF8C8C;
				int tw = c.font.width(s);
				int x = c.w - 5 - tw;
				g.fill(x - 4, c.rightY, c.w - 3, c.rightY + 11, 0x90000000);
				g.fill(c.w - 5 + 2, c.rightY, c.w - 3, c.rightY + 11, c.accent);
				g.drawString(c.font, s, x, c.rightY + 2, color, true);
				c.rightY += 12;
			}
		}
	}

	// ------------------------------------------------------------------ PvP-расходники
	public static final class Consumables extends Feature {
		private static final Item[] ITEMS = {
			Items.TOTEM_OF_UNDYING, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE,
			Items.ENDER_PEARL, Items.FIREWORK_ROCKET
		};

		public Consumables() {
			super("consumables", "PvP-расходники", "PvP consumables", Category.HUD, true);
		}

		@Override
		public void renderHud(GuiGraphics g, HudCtx c) {
			int y = c.h - 30;
			for (Item item : ITEMS) {
				int count = 0;
				for (int i = 0; i < 36; i++) {
					ItemStack s = c.p.getInventory().getItem(i);
					if (s.is(item)) count += s.getCount();
				}
				ItemStack off = c.p.getOffhandItem();
				if (off.is(item)) count += off.getCount();
				if (count <= 0) continue;
				g.renderItem(new ItemStack(item), c.w - 24, y);
				String s = String.valueOf(count);
				g.drawString(c.font, s, c.w - 28 - c.font.width(s), y + 4, 0xFFFFFFFF, true);
				y -= 18;
			}
		}
	}

	// ------------------------------------------------------------------ Инвентарь
	public static final class InventoryHud extends Feature {
		public InventoryHud() {
			super("inventoryhud", "Инвентарь", "Inventory", Category.HUD, false);
		}

		@Override
		public void renderHud(GuiGraphics g, HudCtx c) {
			int x = 5;
			int y = c.leftY;
			int w = 9 * 18 + 4;
			int h = 3 * 18 + 4;
			c.panel(g, x, y, w, h);
			for (int row = 0; row < 3; row++) {
				for (int col = 0; col < 9; col++) {
					ItemStack st = c.p.getInventory().getItem(9 + row * 9 + col);
					if (st.isEmpty()) continue;
					int ix = x + 4 + col * 18;
					int iy = y + 2 + row * 18;
					g.renderItem(st, ix, iy);
					g.renderItemDecorations(c.font, st, ix, iy);
				}
			}
			c.leftY += h + 4;
		}
	}

	// ------------------------------------------------------------------ Индикатор щита
	public static final class ShieldIndicator extends Feature {
		public ShieldIndicator() {
			super("shield", "Индикатор щита", "Shield indicator", Category.HUD, true);
		}

		@Override
		public void renderHud(GuiGraphics g, HudCtx c) {
			ItemStack main = c.p.getMainHandItem();
			ItemStack off = c.p.getOffhandItem();
			ItemStack shield = main.is(Items.SHIELD) ? main : (off.is(Items.SHIELD) ? off : ItemStack.EMPTY);
			if (shield.isEmpty()) return;
			String s;
			int color;
			if (c.p.getCooldowns().isOnCooldown(shield)) {
				s = com.energodar.visuals.Text.tr("Щит: перезарядка", "Shield: cooldown");
				color = 0xFFFF6060;
			} else if (c.p.isBlocking()) {
				s = com.energodar.visuals.Text.tr("Щит: блок", "Shield: blocking");
				color = 0xFF60FF60;
			} else {
				s = com.energodar.visuals.Text.tr("Щит: готов", "Shield: ready");
				color = 0xFFFFFFFF;
			}
			g.drawString(c.font, s, c.w / 2 - 24 - c.font.width(s), c.h / 2 + 10, color, true);
		}
	}

	// ------------------------------------------------------------------ Уведомления
	public static final class Notifications extends Feature {
		public Notifications() {
			super("notifications", "Уведомления", "Notifications", Category.HUD, true);
		}

		@Override
		public void renderHud(GuiGraphics g, HudCtx c) {
			int y = 30;
			for (Notifier.Note n : Notifier.active()) {
				int tw = c.font.width(n.text());
				int x = c.w / 2 - tw / 2 - 5;
				c.panel(g, x, y, tw + 10, 14);
				g.drawString(c.font, n.text(), x + 6, y + 3, 0xFFFFFFFF, true);
				y += 16;
			}
		}
	}

	// ------------------------------------------------------------------ Dynamic Island
	public static final class DynamicIsland extends Feature {
		public DynamicIsland() {
			super("island", "Dynamic Island", "Dynamic Island", Category.HUD, true);
		}

		@Override
		public void renderHud(GuiGraphics g, HudCtx c) {
			List<Notifier.Note> notes = Notifier.active();
			String s = notes.isEmpty()
				? "Energodar  " + c.mc.getFps() + " FPS"
				: notes.get(notes.size() - 1).text();
			int tw = c.font.width(s);
			int pw = tw + 20;
			int x = c.w / 2 - pw / 2;
			int y = 4;
			int ph = 15;
			// имитация скруглённых углов
			g.fill(x + 3, y, x + pw - 3, y + ph, 0xE0000000);
			g.fill(x, y + 3, x + pw, y + ph - 3, 0xE0000000);
			g.fill(x + 1, y + 1, x + pw - 1, y + ph - 1, 0xE0000000);
			g.drawString(c.font, s, x + 10, y + 4, 0xFFFFFFFF, true);
			g.fill(x + 10, y + ph - 2, x + 10 + tw, y + ph - 1, c.accent);
		}
	}
}
