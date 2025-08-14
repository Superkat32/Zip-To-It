package net.superkat.ziptoit;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.GameRules;
import net.superkat.ziptoit.duck.ZipcasterPlayer;
import net.superkat.ziptoit.item.ZipToItItems;
import net.superkat.ziptoit.network.ZipToItPackets;
import net.superkat.ziptoit.network.ZipToItServerNetworkHandler;
import net.superkat.ziptoit.network.packets.AllowZipcastingZipcastS2CPacket;
import net.superkat.ziptoit.particle.ZipToItParticles;
import net.superkat.ziptoit.zipcast.ZipcastManager;
import net.superkat.ziptoit.zipcast.color.StickyHandColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipToIt implements ModInitializer {
	public static final String MOD_ID = "ziptoit";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final GameRules.Key<GameRules.BooleanRule> ALLOW_ZIPCAST_DURING_ZIPCAST =
			GameRuleRegistry.register("allowZipcastDuringZipcast", GameRules.Category.PLAYER,
					GameRuleFactory.createBooleanRule(true, (server, booleanRule) -> {
						server.getWorlds().forEach(world -> {
							world.getPlayers().forEach(player -> {
								ServerPlayNetworking.send(player, new AllowZipcastingZipcastS2CPacket(booleanRule.get()));
							});
						});
					})
			);

	@Override
	public void onInitialize() {
		StickyHandColors.init();

		ZipToItItems.init();
		ZipToItParticles.init();
		ZipToItPackets.init();
		ZipToItServerNetworkHandler.init();

		ServerPlayerEvents.JOIN.register(serverPlayerEntity -> {
			if(serverPlayerEntity instanceof ZipcasterPlayer) {
				ZipcastManager.endZipcast(serverPlayerEntity, true);
				ZipcastManager.endWallStick(serverPlayerEntity, false, true);
			}

			ServerPlayNetworking.send(serverPlayerEntity, new AllowZipcastingZipcastS2CPacket(serverPlayerEntity.getWorld().getGameRules().getBoolean(ALLOW_ZIPCAST_DURING_ZIPCAST)));
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			ServerPlayNetworking.send(newPlayer, new AllowZipcastingZipcastS2CPacket(newPlayer.getWorld().getGameRules().getBoolean(ALLOW_ZIPCAST_DURING_ZIPCAST)));
		});
	}

}