package fr.iut2.stid.gameanalysis;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Classe principale du plugin.
 */
public class PluginMain extends JavaPlugin {

	/** Représente la connexion à la base de données H2 */
	private Connection conn;

	/**
	 * Vrai si le plugin a été chargé correctement, faux sinon. Permet de désactiver
	 * le plugin s'il ne se charge pas correctement, sans avoir des erreurs parce
	 * qu'on essaierait de sauvegarder la base de données.
	 */
	private boolean ok = false;

	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		// Enregistrer les EventListeners ci-dessous:
		pm.registerEvents(new MoveListener(), this);
		pm.registerEvents(new MessageListener(), this);
		pm.registerEvents(new BlockBreakListener(), this);
		pm.registerEvents(new BlockPlaceListener(), this);
		getLogger().info("Chargement de la base de données");
		try {
			initDatabase();
		} catch (ClassNotFoundException | SQLException ex) {
			fatalError(ex, "Impossible de charger la base de données");
		}

		getLogger().info("Plugin chargé !");
		ok = true;
	}

	@Override
	public void onDisable() {
		if (ok) {
			// TODO fermer les PreparedStatements des différents Listeners
		}
		getLogger().info("Plugin déchargé !");
	}

	/**
	 * Signale une erreur fatale au plugin et désactive ce dernier sans essayer d'utiliser la base de données.
	 * @param ex l'erreur
	 * @param msg le message d'erreur
	 */
	private void fatalError(Exception ex, String msg) {
		getLogger().severe(msg);
		ex.printStackTrace();
		getServer().getPluginManager().disablePlugin(this);
	}

	/**
	 * Se connecte à la base de données et crée les tables inexistantes.
	 * @throws SQLException si une erreur de connexion à la BDD ou une erreur SQL survient
	 * @throws ClassNotFoundException si le driver H2 est manquant
	 */
	private void initDatabase() throws SQLException, ClassNotFoundException {
		getLogger().info("Chargement du driver H2");
		Class.forName("org.h2.Driver");

		getLogger().info("Ouverture de la base de données");
		String databasePath = new File(getDataFolder(), "database").getAbsolutePath();
		conn = DriverManager.getConnection("jdbc:h2:" + databasePath);
		conn.setAutoCommit(true); // TODO optimiser en faisant des commits à intervalles réguliers, si besoin

		getLogger().info("Création des tables inexistantes");
		Statement s = conn.createStatement();
		s.executeUpdate("CREATE TABLE IF NOT EXISTS PlayerMoves (Time LONG, ChunkX INT, ChunkY INT, ChunkZ INT)");
		s.executeUpdate("CREATE TABLE IF NOT EXISTS Messages (Size INT)");
		s.executeUpdate("CREATE TABLE IF NOT EXISTS BrokenBlocks (Id INT, PlayerPlayTime LONG)");
		s.executeUpdate("CREATE TABLE IF NOT EXISTS PlacedBlocks (Id INT, PlayerPlayTime LONG)");
		s.close();
	}
}