package fr.iut2.stid.gameanalysis;

import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin {

	@Override
	public void onEnable() {
		getLogger().info("Plugin chargé !");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Plugin déchargé !");
	}
}