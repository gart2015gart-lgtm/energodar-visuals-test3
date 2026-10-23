package com.energodar.visuals;

import com.energodar.visuals.feature.Features;
import net.minecraft.client.Minecraft;

/** Простая локализация: русский / английский. */
public final class Text {
	private Text() {}

	public static boolean ru() {
		try {
			int mode = Features.THEME.language.index; // 0 авто, 1 RU, 2 EN
			if (mode == 1) return true;
			if (mode == 2) return false;
			String code = Minecraft.getInstance().options.languageCode;
			return code != null && (code.startsWith("ru") || code.startsWith("uk") || code.startsWith("be"));
		} catch (Throwable t) {
			return false;
		}
	}

	public static String tr(String ru, String en) {
		return ru() ? ru : en;
	}
}
