package com.energodar.visuals;

import com.energodar.visuals.feature.Category;
import com.energodar.visuals.feature.Feature;
import com.energodar.visuals.feature.Features;
import com.energodar.visuals.feature.Setting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** Меню: вкладки-категории → модули → настройки модуля. Открывается на Right Shift. */
public class ConfigScreen extends Screen {
	private static Category currentCat = Category.HUD;
	private Feature open;

	public ConfigScreen() {
		super(Component.literal("Energodar Visuals"));
	}

	@Override
	protected void init() {
		Category[] cats = Category.values();
		int gap = 2;
		int tabW = Math.min(76, (this.width - 20 - gap * (cats.length - 1)) / cats.length);
		int tx = (this.width - (tabW * cats.length + gap * (cats.length - 1))) / 2;
		for (Category cat : cats) {
			String label = (cat == currentCat ? "§e" : "§f") + cat.title();
			addRenderableWidget(Button.builder(Component.literal(label), b -> {
				currentCat = cat;
				open = null;
				this.rebuildWidgets();
			}).bounds(tx, 26, tabW, 18).build());
			tx += tabW + gap;
		}

		int y0 = 52;
		if (open == null) buildList(y0);
		else buildSettings(y0);

		addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
			.bounds(this.width / 2 - 60, this.height - 26, 120, 20).build());
	}

	// ----------------------------------------------------------------- список модулей
	private void buildList(int y0) {
		List<Feature> list = new ArrayList<>();
		for (Feature f : Features.ALL) {
			if (f.category == currentCat) list.add(f);
		}
		int colW = 134;
		int gap = 4;
		int cols = Math.max(1, Math.min(3, (this.width - 20 + gap) / (colW + gap)));
		int x0 = (this.width - (cols * colW + (cols - 1) * gap)) / 2;

		for (int i = 0; i < list.size(); i++) {
			Feature f = list.get(i);
			int x = x0 + (i % cols) * (colW + gap);
			int y = y0 + (i / cols) * 22;
			boolean hasSettings = !f.settings.isEmpty();
			int mainW = hasSettings ? colW - 22 : colW;

			addRenderableWidget(Button.builder(featureText(f), b -> {
				if (f.toggleable) {
					f.toggle();
					b.setMessage(featureText(f));
				} else {
					open = f;
					this.rebuildWidgets();
				}
			}).bounds(x, y, mainW, 20).build());

			if (hasSettings) {
				addRenderableWidget(Button.builder(Component.literal(">"), b -> {
					open = f;
					this.rebuildWidgets();
				}).bounds(x + mainW + 2, y, 20, 20).build());
			}
		}
	}

	private static Component featureText(Feature f) {
		if (!f.toggleable) return Component.literal(f.name());
		return Component.literal((f.enabled ? "§a" : "§7") + f.name());
	}

	// ----------------------------------------------------------------- настройки модуля
	private void buildSettings(int y0) {
		Feature f = open;
		int x0 = (this.width - (2 * 150 + 6)) / 2;

		addRenderableWidget(Button.builder(Component.literal("< " + Text.tr("Назад", "Back")), b -> {
			open = null;
			this.rebuildWidgets();
		}).bounds(x0, y0, 70, 20).build());

		if (f.toggleable) {
			addRenderableWidget(Button.builder(featureOnOff(f), b -> {
				f.toggle();
				b.setMessage(featureOnOff(f));
			}).bounds(x0 + 74, y0, 110, 20).build());
		}

		int sy = y0 + 28;
		int i = 0;
		for (Setting s : f.settings) {
			int x = x0 + (i % 2) * 156;
			int y = sy + (i / 2) * 22;
			addRenderableWidget(settingWidget(s, x, y, 150));
			i++;
		}
	}

	private static Component featureOnOff(Feature f) {
		return Component.literal(f.name() + ": " + (f.enabled ? "§a" + Text.tr("ВКЛ", "ON") : "§c" + Text.tr("ВЫКЛ", "OFF")));
	}

	private net.minecraft.client.gui.components.AbstractWidget settingWidget(Setting s, int x, int y, int w) {
		if (s instanceof Setting.Bool bool) {
			return Button.builder(boolText(bool), b -> {
				bool.value = !bool.value;
				b.setMessage(boolText(bool));
			}).bounds(x, y, w, 20).build();
		}
		if (s instanceof Setting.Color color) {
			return Button.builder(colorText(color), b -> {
				color.next();
				b.setMessage(colorText(color));
			}).bounds(x, y, w, 20).build();
		}
		if (s instanceof Setting.Mode mode) {
			return Button.builder(modeText(mode), b -> {
				mode.next();
				b.setMessage(modeText(mode));
			}).bounds(x, y, w, 20).build();
		}
		return new IntSlider(x, y, w, 20, (Setting.Int) s);
	}

	private static Component boolText(Setting.Bool s) {
		return Component.literal(s.label() + ": " + (s.value ? "§a" + Text.tr("ВКЛ", "ON") : "§c" + Text.tr("ВЫКЛ", "OFF")));
	}

	private static Component colorText(Setting.Color s) {
		return Component.literal(s.label() + " ").append(Component.literal("■■").withColor(s.value));
	}

	private static Component modeText(Setting.Mode s) {
		return Component.literal(s.label() + ": §e" + s.modeName());
	}

	// ----------------------------------------------------------------- прочее
	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		super.render(g, mouseX, mouseY, delta);
		g.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFF000000 | Features.THEME.accent.value);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void removed() {
		Config.save();
	}

	/** Ползунок целочисленной настройки. */
	private static final class IntSlider extends AbstractSliderButton {
		private final Setting.Int setting;

		IntSlider(int x, int y, int w, int h, Setting.Int setting) {
			super(x, y, w, h, Component.empty(), (double) (setting.value - setting.min) / (setting.max - setting.min));
			this.setting = setting;
			updateMessage();
		}

		@Override
		protected void updateMessage() {
			setMessage(Component.literal(setting.label() + ": " + setting.value));
		}

		@Override
		protected void applyValue() {
			setting.value = setting.min + (int) Math.round(this.value * (setting.max - setting.min));
		}
	}
}
