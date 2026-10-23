package com.energodar.visuals.feature;

import com.energodar.visuals.HudCtx;
import com.energodar.visuals.Notifier;
import com.energodar.visuals.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/** Базовый класс модуля. */
public abstract class Feature {
	public final String id;
	private final String ru;
	private final String en;
	public final Category category;
	public boolean enabled;
	/** false — модуль нельзя выключить (только настройки). */
	public boolean toggleable = true;
	public final List<Setting> settings = new ArrayList<>();

	protected Feature(String id, String ru, String en, Category category, boolean enabledByDefault) {
		this.id = id;
		this.ru = ru;
		this.en = en;
		this.category = category;
		this.enabled = enabledByDefault;
	}

	public String name() {
		return Text.tr(ru, en);
	}

	protected <T extends Setting> T add(T setting) {
		settings.add(setting);
		return setting;
	}

	public void setEnabled(boolean value) {
		if (enabled == value) return;
		enabled = value;
		if (value) onEnable();
		else onDisable();
		Notifier.push(name() + ": " + (value ? Text.tr("включено", "enabled") : Text.tr("выключено", "disabled")));
	}

	public void toggle() {
		setEnabled(!enabled);
	}

	public void onEnable() {}

	public void onDisable() {}

	/** Вызывается каждый клиентский тик, пока модуль включён. */
	public void tick(Minecraft mc) {}

	/** Только для модулей категории HUD. */
	public void renderHud(GuiGraphics g, HudCtx c) {}

	/** Игрок ударил сущность. */
	public void onAttack(Minecraft mc, Entity target) {}
}
