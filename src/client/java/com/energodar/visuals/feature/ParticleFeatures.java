package com.energodar.visuals.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.concurrent.ThreadLocalRandom;

/** Модули категории «Частицы». */
public final class ParticleFeatures {
	private ParticleFeatures() {}

	private static final SimpleParticleType[] TYPES = {
		ParticleTypes.END_ROD, ParticleTypes.FLAME, ParticleTypes.SOUL_FIRE_FLAME,
		ParticleTypes.HAPPY_VILLAGER, ParticleTypes.HEART
	};
	private static final String[] TYPES_RU = {"Стержень", "Огонь", "Души", "Искры", "Сердца"};
	private static final String[] TYPES_EN = {"End rod", "Flame", "Soul", "Sparkles", "Hearts"};

	private static Setting.Mode typeSetting() {
		return new Setting.Mode("type", "Частица", "Particle", TYPES_RU, TYPES_EN, 0);
	}

	// ------------------------------------------------------------------ След
	public static final class Trail extends Feature {
		private final Setting.Mode type = add(typeSetting());
		private final Setting.Int amount = add(new Setting.Int("amount", "Количество", "Amount", 2, 1, 8));

		public Trail() {
			super("trail", "След", "Trail", Category.PARTICLES, false);
		}

		@Override
		public void tick(Minecraft mc) {
			LocalPlayer p = mc.player;
			if (p == null || mc.level == null) return;
			if (p.getDeltaMovement().horizontalDistanceSqr() < 0.0004) return;
			ThreadLocalRandom r = ThreadLocalRandom.current();
			for (int i = 0; i < amount.value; i++) {
				mc.level.addParticle(TYPES[type.index],
					p.getX() + (r.nextDouble() - 0.5) * 0.4, p.getY() + 0.1 + r.nextDouble() * 0.2,
					p.getZ() + (r.nextDouble() - 0.5) * 0.4, 0.0, 0.01, 0.0);
			}
		}
	}

	// ------------------------------------------------------------------ Круги прыжка
	public static final class JumpCircles extends Feature {
		private final Setting.Mode type = add(typeSetting());
		private boolean wasOnGround = true;

		public JumpCircles() {
			super("jumpcircles", "Круги прыжка", "Jump circles", Category.PARTICLES, true);
		}

		@Override
		public void tick(Minecraft mc) {
			LocalPlayer p = mc.player;
			if (p == null || mc.level == null) return;
			boolean ground = p.onGround();
			if (wasOnGround && !ground && p.getDeltaMovement().y > 0.1) {
				for (int i = 0; i < 36; i++) {
					double a = Math.toRadians(i * 10.0);
					double cos = Math.cos(a);
					double sin = Math.sin(a);
					mc.level.addParticle(TYPES[type.index],
						p.getX() + cos * 0.3, p.getY() + 0.05, p.getZ() + sin * 0.3,
						cos * 0.12, 0.0, sin * 0.12);
				}
			}
			wasOnGround = ground;
		}
	}

	// ------------------------------------------------------------------ Нимб
	public static final class Halo extends Feature {
		private final Setting.Mode type = add(typeSetting());
		private int ticks;

		public Halo() {
			super("halo", "Нимб", "Halo", Category.PARTICLES, false);
		}

		@Override
		public void tick(Minecraft mc) {
			LocalPlayer p = mc.player;
			if (p == null || mc.level == null) return;
			if (mc.options.getCameraType().isFirstPerson()) return;
			ticks++;
			double y = p.getY() + p.getBbHeight() + 0.2;
			for (int i = 0; i < 6; i++) {
				double a = ticks * 0.25 + i * (Math.PI * 2.0 / 6.0);
				mc.level.addParticle(TYPES[type.index],
					p.getX() + Math.cos(a) * 0.35, y, p.getZ() + Math.sin(a) * 0.35, 0.0, 0.0, 0.0);
			}
		}
	}

	// ------------------------------------------------------------------ Окружающие частицы
	public static final class Ambient extends Feature {
		private static final SimpleParticleType[] AMBIENT = {
			ParticleTypes.SNOWFLAKE, ParticleTypes.END_ROD, ParticleTypes.ASH
		};
		private final Setting.Mode type = add(new Setting.Mode("type", "Частица", "Particle",
			new String[] {"Снег", "Стержень", "Пепел"}, new String[] {"Snow", "End rod", "Ash"}, 0));
		private final Setting.Int amount = add(new Setting.Int("amount", "Количество", "Amount", 3, 1, 15));
		private final Setting.Int radius = add(new Setting.Int("radius", "Радиус", "Radius", 8, 3, 16));

		public Ambient() {
			super("ambient", "Окружающие частицы", "Ambient particles", Category.PARTICLES, false);
		}

		@Override
		public void tick(Minecraft mc) {
			LocalPlayer p = mc.player;
			if (p == null || mc.level == null) return;
			ThreadLocalRandom r = ThreadLocalRandom.current();
			double rad = radius.value;
			for (int i = 0; i < amount.value; i++) {
				mc.level.addParticle(AMBIENT[type.index],
					p.getX() + (r.nextDouble() - 0.5) * rad * 2.0,
					p.getY() + r.nextDouble() * 5.0 - 1.0,
					p.getZ() + (r.nextDouble() - 0.5) * rad * 2.0,
					0.0, -0.02, 0.0);
			}
		}
	}
}
