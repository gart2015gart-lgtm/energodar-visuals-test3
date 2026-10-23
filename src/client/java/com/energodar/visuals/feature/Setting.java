package com.energodar.visuals.feature;

import com.energodar.visuals.Text;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/** Настройка модуля. Бывает: Bool, Int, Color, Mode. */
public abstract class Setting {
	public final String id;
	private final String ru;
	private final String en;

	protected Setting(String id, String ru, String en) {
		this.id = id;
		this.ru = ru;
		this.en = en;
	}

	public String label() {
		return Text.tr(ru, en);
	}

	public abstract JsonElement save();

	public abstract void load(JsonElement e);

	public static final class Bool extends Setting {
		public boolean value;

		public Bool(String id, String ru, String en, boolean value) {
			super(id, ru, en);
			this.value = value;
		}

		@Override
		public JsonElement save() {
			return new JsonPrimitive(value);
		}

		@Override
		public void load(JsonElement e) {
			value = e.getAsBoolean();
		}
	}

	public static final class Int extends Setting {
		public int value;
		public final int min;
		public final int max;

		public Int(String id, String ru, String en, int value, int min, int max) {
			super(id, ru, en);
			this.value = value;
			this.min = min;
			this.max = max;
		}

		@Override
		public JsonElement save() {
			return new JsonPrimitive(value);
		}

		@Override
		public void load(JsonElement e) {
			value = Math.max(min, Math.min(max, e.getAsInt()));
		}
	}

	public static final class Color extends Setting {
		public static final int[] PALETTE = {
			0xFF3B3B, 0xFF9A1F, 0xFFE23B, 0x4DFF6A, 0x3BE8FF, 0x3B7BFF, 0x7C4DFF, 0xFF4DD2, 0xFFFFFF
		};
		/** RGB без альфа-канала */
		public int value;

		public Color(String id, String ru, String en, int value) {
			super(id, ru, en);
			this.value = value;
		}

		public void next() {
			int idx = 0;
			for (int i = 0; i < PALETTE.length; i++) {
				if (PALETTE[i] == value) {
					idx = i + 1;
					break;
				}
			}
			value = PALETTE[idx % PALETTE.length];
		}

		@Override
		public JsonElement save() {
			return new JsonPrimitive(value);
		}

		@Override
		public void load(JsonElement e) {
			value = e.getAsInt() & 0xFFFFFF;
		}
	}

	public static final class Mode extends Setting {
		private final String[] modesRu;
		private final String[] modesEn;
		public int index;

		public Mode(String id, String ru, String en, String[] modesRu, String[] modesEn, int index) {
			super(id, ru, en);
			this.modesRu = modesRu;
			this.modesEn = modesEn;
			this.index = index;
		}

		public String modeName() {
			return Text.tr(modesRu[index], modesEn[index]);
		}

		public void next() {
			index = (index + 1) % modesRu.length;
		}

		@Override
		public JsonElement save() {
			return new JsonPrimitive(index);
		}

		@Override
		public void load(JsonElement e) {
			int i = e.getAsInt();
			index = (i >= 0 && i < modesRu.length) ? i : 0;
		}
	}
}
