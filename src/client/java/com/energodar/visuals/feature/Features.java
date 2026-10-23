package com.energodar.visuals.feature;

import java.util.ArrayList;
import java.util.List;

/** Реестр всех модулей. Порядок = порядок отрисовки HUD и порядок в меню. */
public final class Features {
	private Features() {}

	public static final List<Feature> ALL = new ArrayList<>();

	private static <T extends Feature> T reg(T f) {
		ALL.add(f);
		return f;
	}

	// Клиент
	public static final MiscFeatures.Theme THEME = reg(new MiscFeatures.Theme());

	// HUD (левая колонка: ватермарк → координаты → инфо → инвентарь; правая: список модулей → эффекты)
	public static final HudFeatures.Watermark WATERMARK = reg(new HudFeatures.Watermark());
	public static final HudFeatures.Coordinates COORDS = reg(new HudFeatures.Coordinates());
	public static final HudFeatures.Info INFO = reg(new HudFeatures.Info());
	public static final HudFeatures.InventoryHud INVENTORY = reg(new HudFeatures.InventoryHud());
	public static final HudFeatures.ModuleList MODULE_LIST = reg(new HudFeatures.ModuleList());
	public static final HudFeatures.PotionsHud POTIONS = reg(new HudFeatures.PotionsHud());
	public static final HudFeatures.ArmorHud ARMOR = reg(new HudFeatures.ArmorHud());
	public static final HudFeatures.TargetHud TARGET = reg(new HudFeatures.TargetHud());
	public static final HudFeatures.ShieldIndicator SHIELD = reg(new HudFeatures.ShieldIndicator());
	public static final HudFeatures.Consumables CONSUMABLES = reg(new HudFeatures.Consumables());
	public static final HudFeatures.Notifications NOTIFICATIONS = reg(new HudFeatures.Notifications());
	public static final HudFeatures.DynamicIsland ISLAND = reg(new HudFeatures.DynamicIsland());

	// Бой
	public static final CombatFeatures.HitEffects HIT_EFFECTS = reg(new CombatFeatures.HitEffects());
	public static final CombatFeatures.HitSound HIT_SOUND = reg(new CombatFeatures.HitSound());
	public static final CombatFeatures.KillEffect KILL_EFFECT = reg(new CombatFeatures.KillEffect());

	// Визуал
	public static final MiscFeatures.Fullbright FULLBRIGHT = reg(new MiscFeatures.Fullbright());

	// Частицы
	public static final ParticleFeatures.Trail TRAIL = reg(new ParticleFeatures.Trail());
	public static final ParticleFeatures.JumpCircles JUMP_CIRCLES = reg(new ParticleFeatures.JumpCircles());
	public static final ParticleFeatures.Halo HALO = reg(new ParticleFeatures.Halo());
	public static final ParticleFeatures.Ambient AMBIENT = reg(new ParticleFeatures.Ambient());

	// Утилиты
	public static final MiscFeatures.ElytraSwapFeature ELYTRA_SWAP = reg(new MiscFeatures.ElytraSwapFeature());
}
