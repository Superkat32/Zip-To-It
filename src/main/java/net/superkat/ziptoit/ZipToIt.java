package net.superkat.ziptoit;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.GameRules;
import net.superkat.ziptoit.compat.GooGunCompat;
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

		if(gooGunLoaded()) {
			GooGunCompat.init();
		}

//		ZipcasterEvents.ZIPCAST_START.register((player, zipcastTarget) -> {
//			LOGGER.info("hi player");
//		});
//
//		ZipcasterEvents.ZIPCAST_END.register((player, zipcastTarget, wasCancelled) -> {
//            LOGGER.info("bye player - {}", wasCancelled);
//		});
//
//		ZipcasterEvents.WALL_STICK_START.register((player, playerTeleportPos, wallBlockPos) -> {
//			LOGGER.info("what's up player");
//			for (BlockPos pos : BlockPos.iterate(player.getBoundingBox().expand(3))) {
//				if(player.getWorld().getBlockState(pos).isOf(Blocks.JUNGLE_PLANKS)) {
//					player.getWorld().breakBlock(pos, false, player);
//				}
//			}
//		});
//
//		ZipcasterEvents.WALL_STICK_END.register((player, wallBlockPos, jumped) -> {
//            LOGGER.info("cya player - {}", jumped);
//		});

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

	public static boolean gooGunLoaded() {
		return FabricLoader.getInstance().isModLoaded("googun");
	}

	public static boolean beADollLoaded() {
		return FabricLoader.getInstance().isModLoaded("be_a_doll");
	}

}