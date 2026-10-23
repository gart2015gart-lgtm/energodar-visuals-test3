package com.energodar.visuals;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

/** Очередь уведомлений (показываются модулями HUD «Уведомления» и «Dynamic Island»). */
public final class Notifier {
	private Notifier() {}

	public record Note(String text, long time) {}

	private static final List<Note> NOTES = new ArrayList<>();

	public static void push(String text) {
		if (Minecraft.getInstance().player == null) return;
		NOTES.add(new Note(text, System.currentTimeMillis()));
		while (NOTES.size() > 5) NOTES.remove(0);
	}

	public static List<Note> active() {
		long now = System.currentTimeMillis();
		NOTES.removeIf(n -> now - n.time() > 3500);
		return NOTES;
	}
}
