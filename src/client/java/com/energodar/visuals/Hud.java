package com.energodar.visuals;

import com.energodar.visuals.feature.Category;
import com.energodar.visuals.feature.Feature;
import com.energodar.visuals.feature.Features;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

/** Отрисовка всех включённых HUD-модулей. */
public final class Hud {
	private Hud() {}

	public static void render(GuiGraphics g, DeltaTracker delta) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer p = mc.player;
		if (p == null || mc.options.hideGui) return;

		HudCtx ctx = new HudCtx(mc, p, g.guiWidth(), g.guiHeight());
		for (Feature f : Features.ALL) {
			if (f.enabled && f.category == Category.HUD) f.renderHud(g, ctx);
		}
	}
}
