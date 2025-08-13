package net.superkat.ziptoit;

import net.fabricmc.api.ModInitializer;
import net.superkat.ziptoit.item.ZipToItItems;
import net.superkat.ziptoit.network.ZipToItPackets;
import net.superkat.ziptoit.network.ZipToItServerNetworkHandler;
import net.superkat.ziptoit.particle.ZipToItParticles;
import net.superkat.ziptoit.zipcast.color.StickyHandColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipToIt implements ModInitializer {
	public static final String MOD_ID = "ziptoit";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		StickyHandColors.init();

		ZipToItItems.init();
		ZipToItParticles.init();
		ZipToItPackets.init();
		ZipToItServerNetworkHandler.init();
	}

}