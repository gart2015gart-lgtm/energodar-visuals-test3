package com.energodar.visuals.feature;

import com.energodar.visuals.Targets;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.concurrent.ThreadLocalRandom;

/** Боевые эффекты. */
public final class CombatFeatures {
	private CombatFeatures() {}

	// ------------------------------------------------------------------ Эффекты удара
	public static final class HitEffects extends Feature {
		private final Setting.Bool crit = add(new Setting.Bool("crit", "Крит-частицы", "Crit particles", true));
		private final Setting.Bool totem = add(new Setting.Bool("totem", "Частицы тотема", "Totem particles", true));
		private final Setting.Int count = add(new Setting.Int("count", "Количество", "Amount", 6, 1, 30));

		public HitEffects() {
			super("hiteffects", "Эффекты удара", "Hit effects", Category.COMBAT, true);
		}

		@Override
		public void onAttack(Minecraft mc, Entity e) {
			if (!(e instanceof LivingEntity) || mc.player == null || mc.level == null) return;
			if (crit.value) {
				mc.player.crit(e);
				mc.player.magicCrit(e);
			}
			if (totem.value) {
				for (int i = 0; i < count.value; i++) {
					mc.level.addParticle(ParticleTypes.TOTEM_OF_UNDYING,
						e.getRandomX(0.6), e.getRandomY(), e.getRandomZ(0.6), 0.0, 0.1, 0.0);
				}
			}
		}
	}

	// ------------------------------------------------------------------ Звук удара
	public static final class HitSound extends Feature {
		private static final SoundEvent[] SOUNDS = {
			SoundEvents.EXPERIENCE_ORB_PICKUP, SoundEvents.PLAYER_LEVELUP, SoundEvents.ARROW_HIT_PLAYER
		};
		private final Setting.Mode sound = add(new Setting.Mode("sound", "Звук", "Sound",
			new String[] {"Опыт", "Уровень", "Стрела"}, new String[] {"Orb", "Level up", "Arrow"}, 0));
		private final Setting.Int volume = add(new Setting.Int("volume", "Громкость", "Volume", 60, 5, 100));
		private final Setting.Int pitch = add(new Setting.Int("pitch", "Тон", "Pitch", 12, 5, 20));

		public HitSound() {
			super("hitsound", "Звук удара", "Hit sound", Category.COMBAT, false);
		}

		@Override
		public void onAttack(Minecraft mc, Entity e) {
			if (!(e instanceof LivingEntity) || mc.level == null) return;
			mc.level.playLocalSound(e.getX(), e.getY(), e.getZ(), SOUNDS[sound.index],
				SoundSource.PLAYERS, volume.value / 100f, pitch.value / 10f, false);
		}
	}

	// ------------------------------------------------------------------ Эффект убийства
	public static final class KillEffect extends Feature {
		private final Setting.Int count = add(new Setting.Int("count", "Количество", "Amount", 30, 5, 80));
		private final Setting.Bool thunder = add(new Setting.Bool("thunder", "Звук грома", "Thunder sound", true));

		public KillEffect() {
			super("killeffect", "Эффект убийства", "Kill effect", Category.COMBAT, true);
		}

		@Override
		public void tick(Minecraft mc) {
			LivingEntity t = Targets.last;
			if (t == null || Targets.killHandled || mc.level == null) return;
			if (System.currentTimeMillis() - Targets.time > 5000) return;
			if (!(t.isDeadOrDying() || t.getHealth() <= 0f)) return;

			Targets.killHandled = true;
			ThreadLocalRandom r = ThreadLocalRandom.current();
			for (int i = 0; i < count.value; i++) {
				mc.level.addParticle(ParticleTypes.TOTEM_OF_UNDYING,
					t.getX(), t.getY() + t.getBbHeight() * 0.5, t.getZ(),
					(r.nextDouble() - 0.5) * 0.6, r.nextDouble() * 0.5, (r.nextDouble() - 0.5) * 0.6);
			}
			for (int i = 0; i < 12; i++) {
				mc.level.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
					t.getX(), t.getY() + 0.2, t.getZ(),
					(r.nextDouble() - 0.5) * 0.2, 0.15 + r.nextDouble() * 0.2, (r.nextDouble() - 0.5) * 0.2);
			}
			if (thunder.value) {
				mc.level.playLocalSound(t.getX(), t.getY(), t.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER,
					SoundSource.PLAYERS, 0.3f, 1.4f, false);
			}
		}
	}
}
