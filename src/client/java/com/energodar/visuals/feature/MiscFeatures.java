package com.energodar.visuals.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/** Визуал / утилиты / клиент. */
public final class MiscFeatures {
	private MiscFeatures() {}

	// ------------------------------------------------------------------ Яркость
	public static final class Fullbright extends Feature {
		private boolean applied;

		public Fullbright() {
			super("fullbright", "Яркость", "Fullbright", Category.VISUAL, false);
		}

		@Override
		public void tick(Minecraft mc) {
			LocalPlayer p = mc.player;
			if (p == null) {
				applied = false;
				return;
			}
			MobEffectInstance cur = p.getEffect(MobEffects.NIGHT_VISION);
			if (cur == null || (applied && cur.getDuration() < 400)) {
				p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0, false, false, false));
				applied = true;
			}
		}

		@Override
		public void onDisable() {
			LocalPlayer p = Minecraft.getInstance().player;
			if (applied && p != null) p.removeEffect(MobEffects.NIGHT_VISION);
			applied = false;
		}
	}

	// ------------------------------------------------------------------ Свап элитры (клавиша V)
	public static final class ElytraSwapFeature extends Feature {
		public ElytraSwapFeature() {
			super("elytraswap", "Свап элитры [V]", "Elytra swap [V]", Category.UTILS, true);
		}
	}

	// ------------------------------------------------------------------ Тема / язык
	public static final class Theme extends Feature {
		public final Setting.Color accent = add(new Setting.Color("accent", "Цвет акцента", "Accent color", 0x7C4DFF));
		public final Setting.Mode language = add(new Setting.Mode("language", "Язык", "Language",
			new String[] {"Авто", "Русский", "English"}, new String[] {"Auto", "Russian", "English"}, 0));

		public Theme() {
			super("theme", "Тема", "Theme", Category.CLIENT, true);
			this.toggleable = false;
		}
	}
}
