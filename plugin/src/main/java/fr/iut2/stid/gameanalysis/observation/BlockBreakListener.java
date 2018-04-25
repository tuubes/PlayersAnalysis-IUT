package fr.iut2.stid.gameanalysis.observation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
/**
 * Listener pour les blocs cassés. Enregistre tous les blocs cassés chaque "ticks" (0,05sec)
 * @author Alexandre
 */
public class BlockBreakListener implements Listener {
	private PreparedStatement insertBlockBreakEvent;

	public BlockBreakListener(Connection conn) throws SQLException {
		insertBlockBreakEvent = conn.prepareStatement("INSERT INTO BrokenBlocks VALUES (?,?)");
	}

	@EventHandler
	public void onPlayerBlockBreak(BlockBreakEvent evt) {
		int idblock = evt.getBlock().getTypeId(); // récupère l'id du bloc lorsqu'il est cassé dans la variable "idblock"
		Player player = evt.getPlayer();
		long ticks = player.getStatistic(Statistic.PLAY_ONE_TICK);
		try {
			insertBlockBreakEvent.setInt(1, idblock);
			insertBlockBreakEvent.setLong(2, ticks);
			insertBlockBreakEvent.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}