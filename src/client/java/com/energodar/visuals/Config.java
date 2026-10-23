package com.energodar.visuals;

import com.energodar.visuals.feature.Feature;
import com.energodar.visuals.feature.Features;
import com.energodar.visuals.feature.Setting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/** Сохранение/загрузка состояния модулей: config/energodar_visuals.json */
public final class Config {
	private Config() {}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("energodar_visuals.json");

	public static void load() {
		try {
			if (Files.exists(FILE)) {
				JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
				for (Feature f : Features.ALL) {
					try {
						if (!root.has(f.id)) continue;
						JsonObject o = root.getAsJsonObject(f.id);
						if (o.has("enabled") && f.toggleable) f.enabled = o.get("enabled").getAsBoolean();
						if (o.has("settings")) {
							JsonObject s = o.getAsJsonObject("settings");
							for (Setting st : f.settings) {
								if (s.has(st.id)) st.load(s.get(st.id));
							}
						}
					} catch (Exception ignored) {
						// битая запись одного модуля не должна ломать остальные
					}
				}
			}
		} catch (Exception e) {
			System.err.println("[Energodar Visuals] Не удалось прочитать конфиг: " + e);
		}
		save();
	}

	public static void save() {
		try {
			JsonObject root = new JsonObject();
			for (Feature f : Features.ALL) {
				JsonObject o = new JsonObject();
				o.addProperty("enabled", f.enabled);
				JsonObject s = new JsonObject();
				for (Setting st : f.settings) s.add(st.id, st.save());
				o.add("settings", s);
				root.add(f.id, o);
			}
			Files.writeString(FILE, GSON.toJson(root));
		} catch (Exception e) {
			System.err.println("[Energodar Visuals] Не удалось сохранить конфиг: " + e);
		}
	}
}
