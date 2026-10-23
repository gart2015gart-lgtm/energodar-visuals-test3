package com.energodar.visuals;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Последняя цель, по которой ударил игрок. */
public final class Targets {
	private Targets() {}

	public static LivingEntity last;
	public static long time;
	public static boolean killHandled;

	public static void hit(Entity e) {
		if (e instanceof LivingEntity le) {
			if (last != le) killHandled = false;
			last = le;
			time = System.currentTimeMillis();
		}
	}
}
