package com.energodar.visuals;

import com.energodar.visuals.feature.Feature;
import com.energodar.visuals.feature.Features;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;

public class EnergodarVisuals implements ClientModInitializer {
	public static final String MOD_ID = "energodar_visuals";

	private static KeyMapping openGui;
	private static KeyMapping elytraSwap;

	@Override
	public void onInitializeClient() {
		Config.load();

		KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));
		openGui = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.energodar_visuals.open_gui", InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, category));
		elytraSwap = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.energodar_visuals.elytra_swap", InputConstants.Type.KEYSYM, InputConstants.KEY_V, category));

		ClientTickEvents.END_CLIENT_TICK.register(EnergodarVisuals::tick);

		HudElementRegistry.attachElementBefore(
			VanillaHudElements.CHAT,
			Identifier.fromNamespaceAndPath(MOD_ID, "hud"),
			Hud::render);

		AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
			if (!(level instanceof ClientLevel)) return InteractionResult.PASS;
			Minecraft mc = Minecraft.getInstance();
			Targets.hit(entity);
			for (Feature f : Features.ALL) {
				if (f.enabled) f.onAttack(mc, entity);
			}
			return InteractionResult.PASS;
		});
	}

	private static void tick(Minecraft mc) {
		while (openGui.consumeClick()) {
			if (mc.screen == null) mc.setScreen(new ConfigScreen());
		}
		while (elytraSwap.consumeClick()) {
			if (Features.ELYTRA_SWAP.enabled) ElytraSwap.swap(mc);
		}
		for (Feature f : Features.ALL) {
			if (f.enabled) f.tick(mc);
		}
	}
}
