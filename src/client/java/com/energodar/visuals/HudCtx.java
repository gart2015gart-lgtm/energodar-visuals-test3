package com.energodar.visuals;

import com.energodar.visuals.feature.Features;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;

/** Контекст рисования HUD: размеры экрана и «курсоры» левой/правой колонок. */
public final class HudCtx {
	public final Minecraft mc;
	public final LocalPlayer p;
	public final Font font;
	public final int w;
	public final int h;
	/** Цвет акцента, ARGB */
	public final int accent;
	public int leftY = 5;
	public int rightY = 5;

	public HudCtx(Minecraft mc, LocalPlayer p, int w, int h) {
		this.mc = mc;
		this.p = p;
		this.font = mc.font;
		this.w = w;
		this.h = h;
		this.accent = 0xFF000000 | Features.THEME.accent.value;
	}

	/** Тёмная плашка с акцентной полосой слева. */
	public void panel(GuiGraphics g, int x, int y, int pw, int ph) {
		g.fill(x, y, x + pw, y + ph, 0x90000000);
		g.fill(x, y, x + 2, y + ph, accent);
	}

	public int ping() {
		ClientPacketListener conn = mc.getConnection();
		if (conn == null) return 0;
		PlayerInfo info = conn.getPlayerInfo(p.getUUID());
		return info == null ? 0 : info.getLatency();
	}
}
