package com.energodar.visuals.feature;

import com.energodar.visuals.Text;

public enum Category {
	HUD("HUD", "HUD"),
	COMBAT("Бой", "Combat"),
	VISUAL("Визуал", "Visual"),
	PARTICLES("Частицы", "Particles"),
	UTILS("Утилиты", "Utils"),
	CLIENT("Клиент", "Client");

	private final String ru;
	private final String en;

	Category(String ru, String en) {
		this.ru = ru;
		this.en = en;
	}

	public String title() {
		return Text.tr(ru, en);
	}
}
