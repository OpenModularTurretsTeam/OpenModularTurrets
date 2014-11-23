package cofh.util;

import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;

public final class ServerHelper {

	private ServerHelper() {

	}

	public static final boolean isClientWorld(World world) {

		return world.isRemote;
	}

	public static final boolean isServerWorld(World world) {

		return !world.isRemote;
	}

	public static boolean isSinglePlayerServer() {

		return FMLCommonHandler.instance().getMinecraftServerInstance() != null;
	}

	public static boolean isMultiPlayerServer() {

		return !isSinglePlayerServer();
	}

}
